package org.remote.common.client;


import org.remote.common.exception.RemoteException;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorRegistrar;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface Client {

    public void invoke(Object data, CallBack callBack) throws RemoteException;

    public Object invoke(Object data) throws RemoteException;

    public Connection getConnection();

    public ProcessorRegistrar getRegistrar();

}
