package org.remote.netty.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;
import org.remote.common.client.Client;
import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.netty.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.remote.common.domain.BaseResponse;

import java.net.ConnectException;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class NettyClientHandler extends IdleStateHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    private NettyClientFactory factory;

    public NettyClientHandler(NettyClientFactory factory, Timer timer, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
        this.factory = factory;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (! (message instanceof BaseHeader)) {
            LOGGER.error("[REMOTE] unsupported message from " + ctx.getChannel().getRemoteAddress());
            ctx.getChannel().close();
            return;
        }
        Client client = factory.query(ctx.getChannel().getRemoteAddress());
        handleMessage(message, client);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        factory.remove(ctx.getChannel().getRemoteAddress());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (e.getCause() instanceof ConnectException) {
            return;
        }
    }

    private void handleMessage(Object message, Client client) {
        BaseHeader header = (BaseHeader) message;
        try {
            ProcessorRegistrar registrar = client.getRegistrar();
            registrar.getExecutor().execute(new MessageHandler(header, client.getConnection(), registrar));
        } catch (Exception e) {
            LOGGER.error("[REMOTE] thread pool is full for " + client.getConnection().getRemoteAddress());
            if (header instanceof BaseRequest) {
                BaseRequest request = (BaseRequest) header;
                BaseResponse response = request.error("thread pool is full.");
                client.getConnection().write(response);
            }
        }
    }
}
