package org.remote.common.codec;

import org.remote.common.exception.CodecsException;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class JavaEncoder implements Encoder {

    @Override
    public byte[] encode(Object object) throws CodecsException {
        try {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(array);
            output.writeObject(object);
            output.flush();
            output.close();
            return array.toByteArray();
        } catch (Exception e) {
            throw new CodecsException(e);
        }
    }
}
