package org.remote.common.codec;

import org.remote.common.exception.RemoteCode;
import org.remote.common.exception.RemoteException;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class JavaDecoder implements Decoder {

    @Override
    public Object decode(byte[] bytes) throws RemoteException {
        try {
            ByteArrayInputStream array = new ByteArrayInputStream(bytes);
            ObjectInputStream input = new ObjectInputStream(array);
            Object object = input.readObject();
            return object;
        } catch (Exception e) {
            throw new RemoteException(RemoteCode.REMOTE_CODECS_DECODE_FAILED, "decode failed.", e);
        }
    }
}
