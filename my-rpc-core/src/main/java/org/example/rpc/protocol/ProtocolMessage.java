package org.example.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    private Header header;
    /**
     * 消息体
     */
    private T body;
@Data
    public static class Header{
        /**
         * 校验数
         */
        private  byte magic;
        /**
         * 版本号
         *
         */
        private byte version;
        /**
         * 序列化器
         */
        private byte serializer;
        /**
         *消息类型
         */
        private byte type;
        /**
         * 响应状态
         */
        private  byte status;
        /**
         * 请求Id
         */
        private long requestId;
        /**
         * 消息体长度
         */
        private int bodyLength;
    }
}
