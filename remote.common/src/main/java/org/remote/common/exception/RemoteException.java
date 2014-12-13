package org.remote.common.exception;


/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class RemoteException extends Exception {

    private RemoteCode code;
    public RemoteException(RemoteCode code, String s, Throwable e) {
        super(s, e);
        this.code = code;
    }

    public RemoteException(RemoteCode code, String s) {
        super(s);
        this.code = code;
    }

    public RemoteException(String s) {
        super(s);
    }

    public RemoteCode getCode() {
        return code;
    }
}
