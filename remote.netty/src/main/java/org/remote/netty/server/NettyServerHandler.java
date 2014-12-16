package org.remote.netty.server;

import org.jboss.netty.channel.*;
import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.netty.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class NettyServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    private ConcurrentHashMap<Channel, NettyConnection> connections;
    private ProcessorRegistrar registrar;

    public NettyServerHandler(ProcessorRegistrar registrar) {
        this.registrar = registrar;
        this.connections = new ConcurrentHashMap<Channel, NettyConnection>();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        connections.put(e.getChannel(), new NettyConnection(e.getChannel()));
        super.channelConnected(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        connections.remove(ctx.getChannel());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (!(message instanceof BaseHeader)) {
            LOGGER.error("[REMOTE] unsupported message from " + ctx.getChannel().getRemoteAddress());
            ctx.getChannel().close();
            return;
        }
        handleMessage(ctx, message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (e.getCause() instanceof ConnectException) {
            return;
        }
    }

    private void handleMessage(final ChannelHandlerContext ctx, final Object message) {
        Connection connection = connections.get(ctx.getChannel());
        BaseHeader header = (BaseHeader) message;
        try {
            registrar.getExecutor().execute(new MessageHandler(header, connection, registrar));
        } catch (Exception e) {
            LOGGER.error("[REMOTE] thread pool is full for " + connection.getRemoteAddress());
            if (header instanceof BaseRequest) {
                BaseRequest request = (BaseRequest) header;
                BaseResponse response = request.error("thread pool is full.");
                connection.write(response);
            }
        }
    }
}
