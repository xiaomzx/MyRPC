package org.example.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    private Object data;
    private Class<?> dataType;
    /**
     * 相应信息
     */
    private String message;
    /**
     * 异常信息
     */
    private Exception exception;
}
