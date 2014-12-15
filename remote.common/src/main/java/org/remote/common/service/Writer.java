package org.remote.common.service;

import org.remote.common.client.ClientCallBack;
import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.server.Connection;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class Writer {

    private ProtocolService protocol;
    private Connection connection;
    private BaseRequest request;

    public Writer(Connection connection, ProtocolService protocol, BaseRequest request) {
        this.protocol = protocol;
        this.connection = connection;
        this.request = request;
    }

    public void request(Object data, ClientCallBack callBack) throws RemoteException {
        BaseHeader request = protocol.buildRequest(data);
        connection.setCallBack(callBack);
        connection.write(request);
    }

    public void response(Object data) throws RemoteException {
        BaseResponse response = protocol.buildResponse(request, data);
        connection.write(response);
    }

    public Connection getConnection() {
        return connection;
    }
}
