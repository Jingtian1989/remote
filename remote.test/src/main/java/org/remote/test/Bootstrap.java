package org.remote.test;

import org.remote.common.client.ClientCallBack;
import org.remote.common.client.Client;
import org.remote.common.client.ClientFactory;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.netty.client.NettyClientFactory;
import org.remote.netty.server.NettyServer;


/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class Bootstrap {

    public static void main(String args[]) {
        ProcessorRegistrar registrar = new ProcessorRegistrar();
        registrar.registerProcessor(new HelloWorldProcessor());
        registrar.registerProcessor(new GoodbyeProcessor());
        NettyServer server = new NettyServer("127.0.0.1", 8009, registrar);
        server.start();

        ClientFactory clientFactory = new NettyClientFactory();
        Client client = null;

        try {
            client = clientFactory.build("127.0.0.1", 8009, registrar);
            HelloWorldPacket hello = new HelloWorldPacket();
            hello.setValue("hello, world!");
            client.invoke(hello, new HelloWorldCallBack());

            System.out.println(client.invoke(hello));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class HelloWorldCallBack implements ClientCallBack {

        @Override
        public void handleResponse(Object data) {
            System.out.println(data);
        }

        @Override
        public void handleException(Exception e) {
            e.printStackTrace();
        }
    }
}
