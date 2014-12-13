package org.remote.common.codec;

import org.remote.common.exception.RemoteCode;
import org.remote.common.exception.RemoteException;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class JavaEncoder implements Encoder {

    @Override
    public byte[] encode(Object object) throws RemoteException {
        try {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(array);
            output.writeObject(object);
            output.flush();
            output.close();
            return array.toByteArray();
        } catch (Exception e) {
            throw new RemoteException(RemoteCode.REMOTE_CODECS_ENCODE_FAILED, "encode failed.", e);
        }
    }
}
