package org.remote.netty.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.remote.common.client.ClientCallBack;
import org.remote.common.domain.BaseHeader;
import org.remote.common.exception.OperationFailedException;
import org.remote.common.server.Connection;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class NettyConnection implements Connection {

    private Channel channel;
    private ClientCallBack callBack;

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
                    if (callBack != null) {
                        callBack.handleException(new OperationFailedException(future.getCause()));
                    }
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public void setCallBack(ClientCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public ClientCallBack getCallBack() {
        return callBack;
    }

    public Channel getChannel() {
        return channel;
    }

}
