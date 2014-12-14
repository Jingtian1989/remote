package org.remote.common.protocol;

import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ProtocolService {

    public BaseHeader buildCommon(Object data) throws RemoteException;

    public BaseRequest buildRequest(Object data) throws RemoteException;

    public BaseResponse buildResponse(BaseRequest request, Object data) throws RemoteException;

}
