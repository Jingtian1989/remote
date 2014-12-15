package org.remote.netty.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.common.thread.NamedThreadFactory;
import org.remote.common.server.Server;
import org.remote.netty.codecs.NettyProtocolDecoder;
import org.remote.netty.codecs.NettyProtocolEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class NettyServer extends Server {

    private final ServerBootstrap bootstrap;
    private final NettyServerHandler handler;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public NettyServer(String host, int port, ProcessorRegistrar registrar) {
        super(host, port, registrar);
        handler = new NettyServerHandler(registrar);
        java.util.concurrent.ThreadFactory master = new NamedThreadFactory("[REMOTE-SERVER-MASTER]");
        java.util.concurrent.ThreadFactory worker = new NamedThreadFactory("[REMOTE-SERVER-WORKER]");
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(master),
                Executors.newCachedThreadPool(worker)));
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
    }

    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.addLast("decoder", new NettyProtocolDecoder());
                pipeline.addLast("encoder", new NettyProtocolEncoder());
                pipeline.addLast("handler", handler);
                return pipeline;
            }
        });
        bootstrap.bind(new InetSocketAddress(getHost(), getPort()));
    }

    @Override
    public void stop() {
        bootstrap.releaseExternalResources();
    }


}
