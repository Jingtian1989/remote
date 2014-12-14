package org.remote.test;

import org.remote.common.annotation.TargetType;
import org.remote.common.service.Processor;
import org.remote.common.service.Writer;

/**
 * Created by jingtian.zjt on 2014/12/13.
 */

@TargetType(value = GoodbyePacket.class)
public class GoodbyeProcessor implements Processor {

    @Override
    public void handle(Object data, Writer writer) {
        GoodbyePacket packet = (GoodbyePacket) data;
        System.out.println(packet.getValue());
    }
}
