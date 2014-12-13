package org.remote.common.protocol;

import org.remote.common.buffer.ByteBufferWrapper;
import org.remote.common.codec.Codecs;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;


/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class PacketRequest extends BaseRequest {

    public static final Protocol protocol = ProtocolFactory.getInstance().getProtocol(PacketProtocol.PACKET_PROTOCOL);

    private byte[] packet;
    public PacketRequest(long requestId, int timeout, byte codecType, byte[] packet) {
        super(PacketProtocol.PACKET_PROTOCOL, requestId, timeout, codecType);
        this.packet = packet;
    }

    public BaseResponse error(String msg) {
        return new PacketResponse(getRequestID(), getCodecType(), ProtocolStatus.ERROR,
                msg.getBytes(ProtocolSetting.DEFAULT_CHARSET));
    }

    @Override
    public BaseResponse response(Object data) throws Exception{
        byte[] packet = Codecs.getEncoder(getCodecType()).encode(data);
        return new PacketResponse(getRequestID(), getCodecType(), ProtocolStatus.OK, packet);

    }

    @Override
    public Object parse() throws Exception {
        return Codecs.getDecoder(getCodecType()).decode(packet);
    }

    public byte[] getPacket() {
        return packet;
    }

    public void encode(ByteBufferWrapper wrapper) throws Exception {
        protocol.encode(this, wrapper);
    }

}
