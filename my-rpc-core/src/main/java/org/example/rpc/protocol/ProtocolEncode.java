package org.example.rpc.protocol;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferImpl;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;
import org.example.rpc.spi.SpiLoader;

import java.io.IOException;

/**
 * 编码器
 */
public class ProtocolEncode {
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException, ClassNotFoundException {
        if(protocolMessage == null || protocolMessage.getHeader() == null){
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendByte(header.getSerializer());
        buffer.appendLong(header.getRequestId());
        //获取序列化器
        ProtocolMessageSerializeeEnum protocolMessageSerializeeEnum = ProtocolMessageSerializeeEnum.getEnumBykey(header.getSerializer());
        if(protocolMessageSerializeeEnum == null){
            throw new RuntimeException("序列化器不存在");
        }

        Serializer serializer = SerializerFactory.getInstant(protocolMessageSerializeeEnum.getVal());
        byte[] bytes = serializer.serializer(protocolMessage.getBody());
        //写入body长度和数据
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
        return  buffer;
    }
}
