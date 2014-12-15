package org.remote.common.protocol;

import org.remote.common.util.UUIDGenerator;
import org.remote.common.codec.Codecs;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;


/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class PacketService implements ProtocolService{


    @Override
    public BaseRequest buildRequest(Object data) throws RemoteException{
        byte[] packet = Codecs.getEncoder(ProtocolSetting.DEFAULT_CODECS).encode(data);
        return new PacketRequest(UUIDGenerator.get(), ProtocolSetting.DEFAULT_TIMEOUT, (byte) ProtocolSetting.DEFAULT_CODECS, packet);
    }

    @Override
    public BaseResponse buildResponse(BaseRequest request, Object data) throws RemoteException {
        byte[] packet = Codecs.getEncoder(request.getCodecType()).encode(data);
        return new PacketResponse(request.getMessageId(), request.getCodecType(), ProtocolStatus.OK, packet);
    }
}
