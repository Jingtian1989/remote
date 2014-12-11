package org.remote.netty.client;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.remote.common.client.BaseClient;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.protocol.ProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class NettyClient extends BaseClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private Channel channel;

    public NettyClient(Channel channel, ProtocolService protocolService) {
        super(protocolService);
        this.channel = channel;
    }

    @Override
    public void send(final BaseRequest request) {
        ChannelFuture future = channel.write(request);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    return;
                } else {
                    channel.close();
                    LOGGER.error("[RPC] send to " + channel.getRemoteAddress() + " failed.", future.getCause());
                    BaseResponse response = request.error("send failed.");
                    complete(response);
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }
}
