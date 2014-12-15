package org.remote.common.protocol;

import org.remote.common.codec.Codecs;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;
import org.remote.common.buffer.ByteBufferWrapper;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public class PacketResponse extends BaseResponse{

    private static final Protocol protocol = ProtocolFactory.getInstance().getProtocol(PacketProtocol.PACKET_PROTOCOL);

    private byte[] packet;
    private ProtocolStatus status;
    public PacketResponse(long requestID, byte codecType, ProtocolStatus status, byte[] packet) {
        super(PacketProtocol.PACKET_PROTOCOL, requestID, codecType);
        this.status = status;
        this.packet = packet;
    }

    public ProtocolStatus getStatus() {
        return status;
    }

    public byte[] getPacket() {
        return packet;
    }

    @Override
    public void encode(ByteBufferWrapper wrapper) throws RemoteException {
        protocol.encode(this, wrapper);
    }

    @Override
    public Object parse() throws RemoteException{
        return Codecs.getDecoder(getCodecType()).decode(packet);
    }
}
