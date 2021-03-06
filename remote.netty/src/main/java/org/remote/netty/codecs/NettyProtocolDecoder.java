package org.remote.netty.codecs;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.remote.common.buffer.ByteBufferWrapper;
import org.remote.common.exception.CodecsException;
import org.remote.common.protocol.Protocol;
import org.remote.common.protocol.ProtocolFactory;
import org.remote.netty.buffer.NettyByteBufferWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class NettyProtocolDecoder extends FrameDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyProtocolDecoder.class);

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {
        int index = buffer.readerIndex();
        if (buffer.readableBytes() < 1) {
            buffer.setIndex(index, buffer.writerIndex());
            return null;
        }
        int type = buffer.readByte();
        Protocol protocol = ProtocolFactory.getInstance().getProtocol(type);
        if (protocol == null) {
            LOGGER.error("[REMOTE] unsupported protocol type: " + type);
        }
        ByteBufferWrapper wrapper = new NettyByteBufferWrapper(buffer, channel);
        return protocol.decode(wrapper, index);
    }
}