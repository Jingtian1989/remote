package org.remote.common.protocol;

import org.remote.common.buffer.ByteBufferWrapper;
import org.remote.common.codec.Codecs;
import org.remote.common.domain.BaseCommon;
import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/14.
 */
public class PacketCommon extends BaseCommon {

    private static final Protocol protocol = ProtocolFactory.getInstance().getProtocol(PacketProtocol.PACKET_PROTOCOL);

    private byte[] packet;
    public PacketCommon(long commonId, byte codecType, byte[] packet) {
        super(PacketProtocol.PACKET_PROTOCOL, commonId, codecType);
        this.packet = packet;
    }

    @Override
    public void encode(ByteBufferWrapper wrapper) throws Exception {
        protocol.encode(this, wrapper);
    }

    public byte[] getPacket() {
        return packet;
    }

    @Override
    public Object parse() throws RemoteException {
        return Codecs.getDecoder(getCodecType()).decode(packet);
    }
}
