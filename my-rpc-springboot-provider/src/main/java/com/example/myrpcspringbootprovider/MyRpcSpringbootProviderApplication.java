package com.example.myrpcspringbootprovider;

import com.example.myrpcspringbootstarter.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class MyRpcSpringbootProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRpcSpringbootProviderApplication.class, args);
    }

}
