package org.remote.common.protocol;
import org.remote.common.codec.Codecs;
import org.remote.common.domain.BaseRequest;
import org.remote.common.util.UUIDGenerator;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class PacketService implements ProtocolService{

    @Override
    public BaseRequest request(Object data) throws Exception{
        byte[] packet = Codecs.getEncoder(ProtocolSetting.DEFAULT_CODECS).encode(data);
        return new PacketRequest(UUIDGenerator.get(), ProtocolSetting.DEFAULT_TIMEOUT, (byte) ProtocolSetting.DEFAULT_CODECS, packet);
    }
}
