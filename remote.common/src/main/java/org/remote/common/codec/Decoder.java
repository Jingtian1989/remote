package org.remote.common.codec;

import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public interface Decoder {

    public Object decode(byte[] bytes) throws RemoteException;
}
