package org.example.rpc.serializer;

import org.example.rpc.spi.SpiLoader;

import java.io.IOException;
import java.util.HashMap;

/**
 * 序列化器工厂（用于获取序列化器对象）
 */
public class SerializerFactory {
    {
        try {
            SpiLoader.load(Serializer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private  final static HashMap<String,Serializer> KEY_SERIALIZER_MAP = new HashMap(){{
     put(SerializerKeys.JDK,new JDKSerializer());
     put(SerializerKeys.JSON,new JsonSerializer());
     put(SerializerKeys.HESSIAN,new HessianSerializer());
     put(SerializerKeys.KRYO,new KyroSerializer());
    }
    };
    //默认选择jdk
    private final static Serializer DefaultSerializer = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);

    public static Serializer getInstant(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }
}
