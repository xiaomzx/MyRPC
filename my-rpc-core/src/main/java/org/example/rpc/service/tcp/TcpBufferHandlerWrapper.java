package org.example.rpc.service.tcp;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
import org.example.rpc.protocol.ProtocolConstant;

/**
 * 使用装饰者模式，对recordParser的buffer封装
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;
   public  TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser = initRecodeParser(bufferHandler);
    }
    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
    private RecordParser initRecodeParser(Handler<Buffer> bufferHandler){
//构造parser
        RecordParser recordParser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        recordParser.setOutput(new Handler<Buffer>() {
            //初始化
            int size =-1;
            //一次性读取
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if(-1 == size){
                    //读取消息体长度
                    size = buffer.getInt(13);
                    recordParser.fixedSizeMode(size);
                    //写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                }else {
                    //写入信息到结果
                    resultBuffer.appendBuffer(buffer);
                    //信息接收完成，处理信息
                    bufferHandler.handle(resultBuffer);
                    //重置一轮
                    recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size=-1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return recordParser;
    }

}
