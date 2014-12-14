package org.remote.test;

import java.io.Serializable;

/**
 * Created by jingtian.zjt on 2014/12/13.
 */
public class GoodbyePacket implements Serializable{

    private static final long serialVersionUID = -2314895387508559393L;
    private String value;

    public GoodbyePacket() {
        value = "Goodbye!";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
