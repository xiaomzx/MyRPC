package com.example.myrpcspringbootconcumer;

import com.example.myrpcspringbootstarter.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class MyRpcSpringbootConcumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRpcSpringbootConcumerApplication.class, args);
    }

}
