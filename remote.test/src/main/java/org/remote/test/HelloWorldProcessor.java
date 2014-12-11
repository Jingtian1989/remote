package org.remote.test;

import org.remote.common.annotation.TargetType;
import org.remote.common.server.Connection;
import org.remote.common.service.Processor;
import org.remote.common.service.ResponseWriter;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */

@TargetType(value = org.remote.test.HelloWorldPacket.class)
public class HelloWorldProcessor implements Processor {
    @Override
    public void handleRequest(Object data, ResponseWriter writer) {
        HelloWorldPacket packet = (HelloWorldPacket) data;
        System.out.println(packet.getValue());
        writer.write("hello, world2!");
    }
}
