package org.example.rpc.protocol;

import io.vertx.core.buffer.Buffer;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;

import java.io.IOException;

public class ProtocolMessageDecoder {
    public static ProtocolMessage decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        //校验
        if(magic != ProtocolConstant.PROTOCOL_MAGIC){
            throw new RuntimeException("消息 magic 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(4));
        header.setType(buffer.getByte(2));
        header.setStatus(buffer.getByte(3));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        //解决粘包问题，只读取指定长度数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //解析消息体
        ProtocolMessageSerializeeEnum serializeeEnum = ProtocolMessageSerializeeEnum.getEnumBykey(header.getSerializer());
        if(serializeeEnum == null){
            throw new RuntimeException("序列化器不存在");
        }
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByValue(header.getType());
        Serializer serializer = SerializerFactory.getInstant(serializeeEnum.getVal());
        if(messageTypeEnum == null){
            throw new RuntimeException("消息类型不存在");
        }
        switch (messageTypeEnum){
            case REQUEST:
                RpcRequest request = serializer.deserializer(bodyBytes, RpcRequest.class);
                return new ProtocolMessage(header,request);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserializer(bodyBytes, RpcResponse.class);
                return new ProtocolMessage(header,rpcResponse);
            case HEART_BEAT:
            case OTHER:
            default:throw new RuntimeException("占卜支持消息类型");
        }
    }
}
