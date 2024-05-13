package org.example.rpc.registry;

import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class EtcdRegistry implements  Registry{
    private   Client client;
    private KV kvClient;
    /**
     * 键储存的根节点
     */
    public static  final String ETCD_ROOT_PATH = "/rpc/";


    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddr())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        //创建一个租约client，控制时间
        Lease leaseClient = client.getLeaseClient();
        //30秒的租约
        long id = leaseClient.grant(300).get().getID();

        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(id).build();
        kvClient.put(key,value,putOption).get();

    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        kvClient.delete(ByteSequence.from( ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }

    /**
     * 根据服务名称作为前缀，从Etcd获取服务下的节点列表
     * @param serviceKey
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscover(String serviceKey) {
        //前缀
        String searchPrefix = ETCD_ROOT_PATH+serviceKey+"/";

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),getOption).get().getKvs();

            return  keyValues.stream().map(keyValue ->{
                String s = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(s, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destory() {
        System.out.println("当前节点下线");
        if(kvClient != null){
            kvClient.close();
        }
        if (client != null){
            client.close();
        }
    }

}
