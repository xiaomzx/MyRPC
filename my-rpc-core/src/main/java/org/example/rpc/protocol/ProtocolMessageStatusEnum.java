package org.example.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok",20),
    BAD_RRQUEST("badRequest",40),
    BAD_RESPONSE("badResponse",50);
    private final String text;
    private final int value;

    ProtocolMessageStatusEnum(String text,int value) {
        this.text = text;
        this.value = value;
    }

    public static ProtocolMessageStatusEnum getEnumByValue(int value){
        for(ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()){
            if(anEnum.getValue() == value){
                return anEnum;
            }
        }
        return null;
    }
}
