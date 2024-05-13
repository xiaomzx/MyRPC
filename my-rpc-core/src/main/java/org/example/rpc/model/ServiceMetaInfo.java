package org.example.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMetaInfo {
    private  String serviceName;
    private String serviceVersion="1.0";
    /**
     * 服务域名
     */
    private String serviceHost;
    /**
     * 服务端口
     */
    private String servicePort;
    private String serviceGroup ="default";

    /**
     * 获取服务键名
     * @return
     */
    public String getServiceKey(){
        return  String.format("%s:%s",serviceName,serviceVersion);
    }

    public String getServiceNodeKey(){
        return String.format("%s/%s:%s",getServiceKey(),serviceHost, servicePort);
    }

    public String getServiceAddress(){
        if(!serviceHost.contains("http")){
            return String.format("http://%s:%s",serviceHost,servicePort);
        }
        return String.format("%s:%s",serviceHost,servicePort);
    }
}
