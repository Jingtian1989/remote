package org.remote.common.domain;


import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseResponse extends BaseHeader {

    public BaseResponse(int protocolType, long requestID, byte codecType) {
        super(protocolType, requestID, codecType);
    }

    public abstract Object parse() throws RemoteException;
}
