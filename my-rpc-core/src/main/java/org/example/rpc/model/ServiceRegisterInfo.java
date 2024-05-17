package org.example.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo <T>{
    private String serviceName;
    //实现类
    private Class<? extends T> implClass;
}
