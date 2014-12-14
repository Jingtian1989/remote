package org.remote.common.domain;

import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/14.
 */
public abstract class BaseCommon extends BaseHeader {
    public BaseCommon(int protocolType, long messageId, byte codecType) {
        super(protocolType, messageId, codecType);
    }

    public abstract Object parse() throws RemoteException;
}
