package org.remote.netty.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.remote.common.client.CallBack;
import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseResponse;
import org.remote.common.server.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class NettyConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConnection.class);
    private Channel channel;
    private CallBack callBack;

    public NettyConnection(Channel channel) {
        this.channel = channel;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public void write(BaseHeader header) {
        ChannelFuture future = channel.write(header);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LOGGER.error("[REMOTE] write to connection " + future.getChannel().getRemoteAddress() + " failed.");
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public CallBack getCallBack() {
        return callBack;
    }

    public Channel getChannel() {
        return channel;
    }

}
