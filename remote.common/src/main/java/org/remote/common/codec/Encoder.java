package org.remote.common.codec;

import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public interface Encoder {

    public byte[] encode(Object object) throws RemoteException;
}
