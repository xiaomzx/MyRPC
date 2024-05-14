package org.example.rpc.protocol;

public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH =17;
    /**
     * 协议校验数
     */
    byte PROTOCOL_MAGIC = 0X1;
    /**
     * 协议版本号
     *
     */
    byte PROTOCOL_VERSION=0X1;
}
