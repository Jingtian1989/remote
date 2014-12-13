package org.remote.test;

import org.remote.common.client.Client;
import org.remote.common.client.ClientFactory;
import org.remote.common.protocol.PacketService;
import org.remote.common.service.ProcessorService;
import org.remote.netty.client.NettyClientFactory;
import org.remote.netty.server.NettyServer;

import java.net.InetSocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class Bootstrap {

    public static void main(String args[]) {
        ProcessorService processor = new ProcessorService();
        processor.registerProcessor(new HelloWorldProcessor());
        processor.registerProcessor(new GoodbyeProcessor());
        NettyServer server = new NettyServer("127.0.0.1", 8009, processor);
        server.start();

        PacketService packet = new PacketService();
        ClientFactory clientFactory = new NettyClientFactory();
        Client client = null;
        try {
            client = clientFactory.build(new InetSocketAddress("127.0.0.1", 8009), packet, processor);
            HelloWorldPacket hello = new HelloWorldPacket();
            hello.setValue("hello, world!");
            client.invoke(hello);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
