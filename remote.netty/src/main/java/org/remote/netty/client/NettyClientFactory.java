package org.remote.netty.client;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.remote.common.client.BaseClientFactory;
import org.remote.common.client.Client;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.protocol.ProtocolSetting;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.common.thread.NamedThreadFactory;
import org.remote.netty.codecs.NettyProtocolDecoder;
import org.remote.netty.codecs.NettyProtocolEncoder;
import org.remote.netty.server.NettyConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class NettyClientFactory extends BaseClientFactory {

    private final ClientBootstrap bootstrap;

    public NettyClientFactory() {
        ThreadFactory master = new NamedThreadFactory("[REMOTE-CLIENT-MASTER]");
        ThreadFactory worker = new NamedThreadFactory("[REMOTE-CLIENT-WORKER]");
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(master),
                Executors.newCachedThreadPool(worker)));
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.addLast("decoder", new NettyProtocolDecoder());
                pipeline.addLast("encoder", new NettyProtocolEncoder());
                pipeline.addLast("handler", new NettyClientHandler(NettyClientFactory.this,
                        new HashedWheelTimer(), ProtocolSetting.DEFAULT_MAX_IDLE, 0, 0));
                return pipeline;
            }
        });
    }

    @Override
    public Client connect(SocketAddress address, int timeout, ProtocolService protocol, ProcessorRegistrar registrar) throws ConnectException {
        ChannelFuture future = bootstrap.connect(address);
        future.awaitUninterruptibly(timeout);
        if (!future.isDone()) {
            throw new ConnectException();
        }
        if (!future.isSuccess()) {
            throw new ConnectException();
        }

        if (!future.getChannel().isConnected()) {
            throw new ConnectException();
        }
        NettyConnection connection = new NettyConnection(future.getChannel());
        NettyClient client = new NettyClient(connection, protocol, registrar);
        return client;
    }
}
