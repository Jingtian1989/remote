package org.remote.common.client;

import org.remote.common.exception.RemoteException;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ClientCallBack {

    public void handle(Object data);

    public void exception(RemoteException e);

}
