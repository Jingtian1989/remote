package org.remote.netty.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class NettyServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private ProcessorService processor;
    public NettyServerHandler(ProcessorService processor) {
        this.processor = processor;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (! (message instanceof BaseRequest)) {
            LOGGER.error("[REMOTE] unsupported message type from " + ctx.getChannel().getRemoteAddress());
            throw new Exception("unsupported message type from " + ctx.getChannel().getRemoteAddress());
        }
        handleRequest(ctx, message);
    }


    private void handleRequest(final ChannelHandlerContext ctx, final Object message) {
        final Connection connection = new NettyConnection(ctx.getChannel());
        BaseRequest request = (BaseRequest)message;
        if (processor.getExecutor(request) == null) {
            try {
                processor.handleRequest(request, connection);
            } catch (Exception e) {
                LOGGER.error("[REMOTE] unexpected application exception when handle the request. exception:", e);
                BaseResponse response = request.error("unexpected application exception @"
                        + connection.getLocalAddress());
                connection.write(response);
            }
        } else {
            try {
                processor.getExecutor(request).execute(new Handler(request, connection, processor));
            } catch (RejectedExecutionException e) {
                LOGGER.error("[REMOTE] thread pool is full for " + connection.getRemoteAddress());
                BaseResponse response = request.error("thread pool is full.");
                connection.write(response);
            } catch (NullPointerException e) {
                LOGGER.error("[REMOTE] there is executable command in request " + connection.getRemoteAddress());
                BaseResponse response = request.error("no command.");
                connection.write(response);
            }
        }
    }

    public static class Handler implements Runnable {
        private final Connection connection;
        private final ProcessorService service;
        private final BaseRequest request;
        private final long dispatchTime = System.currentTimeMillis();

        public Handler(BaseRequest request, Connection connection, ProcessorService service) {
            this.request = request;
            this.connection = connection;
            this.service = service;
        }

        @Override
        public void run() {
            try {
                long begin = System.currentTimeMillis();
                long pending = begin - dispatchTime;
                int clientTimeout = request.getTimeout();
                if (clientTimeout > 0 && pending >= clientTimeout) {
                    LOGGER.error("[REMOTE] drop timeout client request from " + connection.getRemoteAddress() + " pending time " + pending);
                    return;
                }
                service.handleRequest(request, connection);
            } catch (Exception e) {
                LOGGER.error("[REMOTE] unexpected application exception when hanle the request. exception:", e);
                BaseResponse response = request.error("unexpected application exception @" +
                        connection.getLocalAddress());
                connection.write(response);
            }
        }
    }
}
