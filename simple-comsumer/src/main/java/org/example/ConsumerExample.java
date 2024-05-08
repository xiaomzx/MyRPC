package org.example;

import org.example.rpc.config.RpcConfig;
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
    }
}
