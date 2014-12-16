package org.remote.common.client;


import org.remote.common.exception.CodecsException;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorRegistrar;

import java.util.concurrent.TimeoutException;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface Client {

    public void invoke(Object data, ClientCallBack callBack);

    public Object invoke(Object data) throws CodecsException, InterruptedException, TimeoutException;

    public Connection getConnection();

    public ProcessorRegistrar getRegistrar();

}
