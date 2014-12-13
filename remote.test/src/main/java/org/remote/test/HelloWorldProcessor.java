package org.remote.test;

import org.remote.common.annotation.TargetType;
import org.remote.common.domain.BaseRequest;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolFactory;
import org.remote.common.service.Processor;
import org.remote.common.service.ResponseWriter;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */

@TargetType(value = HelloWorldPacket.class)
public class HelloWorldProcessor implements Processor {
    @Override
    public void handleRequest(Object data, ResponseWriter writer) {
        HelloWorldPacket packet = (HelloWorldPacket) data;
        System.out.println(packet.getValue());

        GoodbyePacket hello = new GoodbyePacket();
        try {
            BaseRequest request = ProtocolFactory.getInstance().getProtocolService(writer.getRequest().getProtocolType()).build(hello);
            writer.getConnection().write(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
