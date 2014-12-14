package org.remote.common.domain;

import org.remote.common.buffer.ByteBufferWrapper;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseHeader {

    private final int protocolType;
    private final long messageId;
    private byte codecType;

    public BaseHeader(int protocolType, long messageId, byte codecType) {
        this.protocolType = protocolType;
        this.messageId = messageId;
        this.codecType = codecType;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public long getMessageId() {
        return messageId;
    }

    public abstract void encode(ByteBufferWrapper wrapper) throws Exception;

    public byte getCodecType() {
        return codecType;
    }
}
