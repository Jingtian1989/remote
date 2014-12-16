package org.remote.common.protocol;

import org.remote.common.buffer.ByteBufferWrapper;
import org.remote.common.codec.Codecs;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.CodecsException;


/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class PacketRequest extends BaseRequest {

    private static final Protocol protocol = ProtocolFactory.getInstance().getProtocol(PacketProtocol.PACKET_PROTOCOL);
    private byte[] packet;

    public PacketRequest(long requestId, int timeout, byte codecType, byte[] packet) {
        super(PacketProtocol.PACKET_PROTOCOL, requestId, timeout, codecType);
        this.packet = packet;
    }

    public BaseResponse error(String msg) {
        return new PacketResponse(getMessageId(), getCodecType(), ProtocolStatus.ERROR,
                msg.getBytes(ProtocolSetting.DEFAULT_CHARSET));
    }

    @Override
    public Object parse() throws CodecsException {
        return Codecs.getDecoder(getCodecType()).decode(packet);
    }

    public byte[] getPacket() {
        return packet;
    }

    public void encode(ByteBufferWrapper wrapper){
        protocol.encode(this, wrapper);
    }

}
