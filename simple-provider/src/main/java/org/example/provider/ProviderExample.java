package org.example.provider;

import org.example.common.services.UserService;
import org.example.rpc.bootstaro.ProviderBootStrap;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.model.ServiceRegisterInfo;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.service.HttpServer;
import org.example.rpc.service.VertxHttpServer;
import org.example.rpc.service.tcp.VertxTcpClient;
import org.example.rpc.service.tcp.VertxTcpServer;
import org.example.rpc.spi.SpiLoader;
import org.example.rpc.utils.ConfigUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProviderExample {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<ServiceRegisterInfo<?>> serviceMetaInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceMetaInfoList.add(serviceRegisterInfo);
        ProviderBootStrap.init(serviceMetaInfoList);
    }
}
