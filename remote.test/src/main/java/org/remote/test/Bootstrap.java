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
        ProcessorService service = new ProcessorService();
        service.registerProcessor(new HelloWorldProcessor());
        NettyServer server = new NettyServer("127.0.0.1", 8009, service);
        server.start();

        PacketService packetService = new PacketService();
        ClientFactory clientFactory = new NettyClientFactory(packetService);
        Client client = null;
        try {
            client = clientFactory.get(new InetSocketAddress("127.0.0.1", 8009), true);
            HelloWorldPacket packet = new HelloWorldPacket();
            packet.setValue("hello, world!");
            String result = (String)client.syncInvoke(packet);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
