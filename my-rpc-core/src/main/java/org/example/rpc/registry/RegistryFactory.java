package org.example.rpc.registry;

import org.example.rpc.spi.SpiLoader;

import java.io.IOException;

/**
 * 用于获取注册中心对象
 */
public class RegistryFactory {
    static {
        try {
            SpiLoader.load(Registry.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 默认注册中心
     */
        private  final static Registry DEFAULT_REGISTRY= new EtcdRegistry();
        public static Registry getInstance(String key){
            return SpiLoader.getInstance(Registry.class,key);
        }
}
