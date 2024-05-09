package org.example.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;

import java.io.IOException;

public class JsonSerializer implements  Serializer{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public <T> byte[] serializer(T object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserializer(byte[] bytes, Class<T> classType) throws IOException {
        T obj = objectMapper.readValue(bytes, classType);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, classType);
        }
        if (obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, classType);
        }
        return obj;
    }

    /**
     * object数组再序列化时，会被擦除，需要特俗处理
     * @param request
     * @param type
     * @return
     * @param <T>
     */
    private <T> T handleRequest(RpcRequest request ,Class<T> type) throws IOException {
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] args = request.getArgs();
        for(int i=0;i< parameterTypes.length;i++){
            Class<?> clazz = parameterTypes[i];
            //类型不同，则重新处理
            if(clazz.isAssignableFrom(args[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(args[i]);
               args[i]= objectMapper.readValue(bytes,clazz);
            }
        }
        return type.cast(request);
    }

    /**
     * object数组再序列化时，会被擦除，需要特俗处理
     * @param response
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleResponse(RpcResponse response,Class<T> type) throws IOException {
        Object data = response.getData();
        Class<?> dataType = response.getDataType();
        byte[] bytes = objectMapper.writeValueAsBytes(data);
        response.setData(objectMapper.readValue(bytes,dataType));
        return  type.cast(response);
    }
}
