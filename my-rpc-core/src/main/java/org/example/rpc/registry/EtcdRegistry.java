package org.example.rpc.registry;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class EtcdRegistry implements  Registry{
    private   Client client;
    private KV kvClient;
    /**
     * 本地注册的节点Key
     */
    private final Set<String> localRegistryNodeKeySet = new HashSet<>();
    /**
     * 键储存的根节点
     */
    public static  final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 服务缓存
     */
    public static RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 监听的key集合
     */
    public static Set<String> watchingkeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddr())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        //创建一个租约client，控制时间
        Lease leaseClient = client.getLeaseClient();
        //30秒的租约
        long id = leaseClient.grant(30).get().getID();

        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(id).build();
        kvClient.put(key,value,putOption).get();
        //添加节点信息到本地
        localRegistryNodeKeySet.add(registryKey);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String source = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(source, StandardCharsets.UTF_8));
        //移除本地节点信息
        localRegistryNodeKeySet.remove(source);
    }

    /**
     * 根据服务名称作为前缀，从Etcd获取服务下的节点列表
     * @param serviceKey
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscover(String serviceKey) {
        //先从缓存查找服务
        List<ServiceMetaInfo> ServiceCacheList = registryServiceCache.getNewServiceCache();
       if(ServiceCacheList != null && !ServiceCacheList.isEmpty() ){
           return ServiceCacheList;
       }

        //前缀
        String searchPrefix = ETCD_ROOT_PATH+serviceKey+"/";
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),getOption).get().getKvs();

            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(keyValue -> {
                String s = keyValue.getKey().toString(StandardCharsets.UTF_8);
                //监听
                watch(s);
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
            //写入缓存
            registryServiceCache.setNewServiceCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destory() {
        System.out.println("当前节点下线");
        //遍历节点
        for(String key:localRegistryNodeKeySet){
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key+"节点下线失败");
            }
        }
        if(kvClient != null){
            kvClient.close();
        }
        if (client != null){
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        //每10秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
            //遍历key
            for(String key :localRegistryNodeKeySet){
                try {
                    List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                            .get().getKvs();
                    //节点已过期，需要重新注册
                    if(CollectionUtil.isEmpty(keyValues)){
                        continue;
                    }
                    //节点未过期，续签
                    KeyValue keyValue = keyValues.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException("续签失败",e);
                }
            }
            }
        });
        //兼容Quartz表达式
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String key) {
        Watch watchClient = client.getWatchClient();
        //是否已经监听了
        boolean add = watchingkeySet.add(key);
        if(add){
            watchClient.watch(ByteSequence.from(key,StandardCharsets.UTF_8),
                    watchResponse -> {
                for(WatchEvent event:watchResponse.getEvents()){
                    switch (event.getEventType()){
                        case DELETE:
                            //清理缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                            break;
                        default: break;
                    }
                }
                    });
        }

    }

}
