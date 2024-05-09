package org.example;

import org.example.common.services.UserService;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.proxy.ServiceProxyFactory;
import org.example.rpc.utils.ConfigUtils;

/**
 * Hello world!
 *
 */
public class ConsumerExample
{
    public static void main( String[] args )
    {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println( rpc );
        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);
        short nnumber = userService.getNnumber();
        System.out.println(nnumber);
    }
}
