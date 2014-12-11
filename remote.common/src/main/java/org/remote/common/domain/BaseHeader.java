package org.remote.common.domain;

import org.remote.common.buffer.ByteBufferWrapper;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseHeader {

    private final int protocolType;
    private final long requestID;
    private byte codecType;

    public BaseHeader(int protocolType, long requestID, byte codecType) {
        this.protocolType = protocolType;
        this.requestID = requestID;
        this.codecType = codecType;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public long getRequestID() {
        return requestID;
    }

    public abstract void encode(ByteBufferWrapper wrapper) throws Exception;

    public byte getCodecType() {
        return codecType;
    }
}
