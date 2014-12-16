package org.remote.netty.codecs;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.remote.common.domain.BaseHeader;
import org.remote.netty.buffer.NettyByteBufferWrapper;


/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NettyProtocolEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) {
        NettyByteBufferWrapper wrapper = new NettyByteBufferWrapper();
        ((BaseHeader)msg).encode(wrapper);
        return wrapper.getBuffer();
    }
}
