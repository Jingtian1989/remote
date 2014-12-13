package org.remote.common.protocol;

import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ProtocolService {

    public BaseRequest build(Object data) throws RemoteException;

}
