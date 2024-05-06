package org.example.common.model;

import java.io.Serializable;

//需要序列化方便网络传输
public class User implements Serializable {

    private  String name;
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
