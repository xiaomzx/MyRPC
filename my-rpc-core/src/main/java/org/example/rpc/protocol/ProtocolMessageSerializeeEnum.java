package org.example.rpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 协议序列化器枚举
 */
@Getter
public enum ProtocolMessageSerializeeEnum {
    JDK(0,"jdk"),
    JSON(1,"json"),
    KRYO(2,"kryo"),
    HESSIAN(3,"hessian");
    private final int key;
    private final String val;
    ProtocolMessageSerializeeEnum(int key,String val){
        this.key = key;
        this.val = val;
    }

    /**
     * 根据val获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageSerializeeEnum getEnumByvalue(String value){
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        for(ProtocolMessageSerializeeEnum pEnum : ProtocolMessageSerializeeEnum.values()){
            if(pEnum.getVal().equals(value)){
                return pEnum;
            }
        }
        return  null;
    }

    /**
     * 根据key获取枚举
     * @param key
     * @return
     */
    public static ProtocolMessageSerializeeEnum getEnumBykey(int key){
        for(ProtocolMessageSerializeeEnum pEnum : ProtocolMessageSerializeeEnum.values()){
            if(pEnum.getKey() == key){
                return pEnum;
            }
        }
        return  null;
    }

    public static List<String> getvalues(){
        return Arrays.stream(ProtocolMessageSerializeeEnum.values()).map(e->e.getVal()).collect(Collectors.toList());
    }
}
