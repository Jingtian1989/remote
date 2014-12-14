package org.remote.test;

import org.remote.common.client.Client;
import org.remote.common.client.ClientFactory;
import org.remote.common.service.ProcessorService;
import org.remote.netty.client.NettyClientFactory;
import org.remote.netty.server.NettyServer;


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

        ClientFactory clientFactory = new NettyClientFactory();
        Client client = null;
        try {
            client = clientFactory.build("127.0.0.1", 8009, processor);
            HelloWorldPacket hello = new HelloWorldPacket();
            hello.setValue("hello, world!");
            System.out.println(client.invoke(hello));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
