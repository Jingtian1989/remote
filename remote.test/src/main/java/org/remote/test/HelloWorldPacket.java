package org.remote.test;

import java.io.Serializable;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class HelloWorldPacket implements Serializable{

    private static final long serialVersionUID = -7374759541554695427L;
    private String value;

    public HelloWorldPacket() {
        value = "hello world!";
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
