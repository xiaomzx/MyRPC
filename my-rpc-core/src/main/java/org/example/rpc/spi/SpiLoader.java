package org.example.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类：接口名（文件名） =>（key => 实现类 ）
     */
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    /**
     * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有SPI
     *
     */
    public static void loadAll() throws IOException, ClassNotFoundException {
        log.info("加载所有SPI");
        for(Class c : LOAD_CLASS_LIST){
            load(c);
        }
    }
    /**
     * 获取某个接口的实例
     *
     * @param tClass
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<T> tClass, String key) {
        String tClassName = tClass.getName();
        //根据接口名获取接口所需的序列化对象
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }
    public static Map<String ,Class<?>> load(Class<?> loadClass) throws IOException, ClassNotFoundException {
        log.info("加载类型为{}的SPI",loadClass.getName());
        //扫描路径，先用户的自定义后系统的
        Map<String ,Class<?>> keyClassMap = new HashMap<>();
        for(String dir:SCAN_DIRS){
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            //读取每个资源文件
            for(URL resource :resources){
                InputStreamReader  inputStreamReader = new InputStreamReader(resource.openStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while((line = bufferedReader.readLine())!= null){
                    String[] split = line.split("=");
                    if(split.length >1){
                        String key = split[0];
                        String className = split[1];
                        keyClassMap.put(key,Class.forName(className));
                    }
                }

            }

        }
        loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;

    }

    //测试
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        loadAll();
        System.out.println(loaderMap);
        Serializer serializer = getInstance(Serializer.class, "hessian");
        System.out.println(serializer);
    }
}
