package org.remote.test;

import org.remote.common.annotation.TargetType;
import org.remote.common.exception.RemoteException;
import org.remote.common.service.Processor;
import org.remote.common.service.Writer;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */

@TargetType(value = HelloWorldPacket.class)
public class HelloWorldProcessor implements Processor {
    @Override
    public void handle(Object data, Writer writer) {
        HelloWorldPacket packet = (HelloWorldPacket) data;
        System.out.println(packet.getValue());

        try {
            writer.response("hello, world2!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            GoodbyePacket hello = new GoodbyePacket();
            writer.write(hello);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
