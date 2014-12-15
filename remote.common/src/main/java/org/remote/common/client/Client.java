package org.remote.common.client;


import org.remote.common.exception.RemoteException;
import org.remote.common.server.Connection;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface Client {

    public Object invoke(Object data) throws RemoteException;

    public void invoke(Object data, ClientCallBack callBack) throws RemoteException;

    public void write(Object data) throws RemoteException;

    public Connection connection();

}
