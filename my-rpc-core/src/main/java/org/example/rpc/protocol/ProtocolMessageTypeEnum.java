package org.example.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息类型枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHER(3);
    private final int key;
    ProtocolMessageTypeEnum(int key){
        this.key = key;
    }

    /**
     * 根据key获取枚举
     * @param key
     * @return
     */
    public static  ProtocolMessageTypeEnum getEnumByValue(int key){
        for(ProtocolMessageTypeEnum penum:ProtocolMessageTypeEnum.values()){
            if(penum.getKey() == key){
                return penum;
            }
        }
        return  null;
    }
}
