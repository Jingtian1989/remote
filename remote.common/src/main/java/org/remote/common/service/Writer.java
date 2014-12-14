package org.remote.common.service;

import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolFactory;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.server.Connection;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class Writer {

    private ProtocolService protocol;
    private Connection connection;
    private BaseHeader header;

    public Writer(Connection connection, BaseHeader header) {
        this.protocol = ProtocolFactory.getInstance().getProtocolService(header.getProtocolType());
        this.connection = connection;
        this.header = header;
    }

    public void response(Object data) throws RemoteException {
        BaseResponse response = protocol.buildResponse((BaseRequest)header, data);
        connection.write(response);
    }

    public void request(Object data) throws RemoteException {
        BaseRequest request = protocol.buildRequest(data);
        connection.write(request);
    }

    public void write(Object data) throws RemoteException {
        BaseHeader common = protocol.buildCommon(data);
        connection.write(common);
    }
}
