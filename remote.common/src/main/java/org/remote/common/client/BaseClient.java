package org.remote.common.client;

import org.remote.common.domain.BaseCommon;
import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public abstract class BaseClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseClient.class);

    private Map<Long, ClientCallBack> responses;
    private Connection connection;
    private ProtocolService protocol;
    private ProcessorService processor;


    public BaseClient(Connection connection, ProtocolService protocol, ProcessorService processor) {
        this.connection = connection;
        this.protocol = protocol;
        this.processor = processor;
        this.responses = new ConcurrentHashMap<Long, ClientCallBack>();
    }

    @Override
    public Object invoke(Object data) throws RemoteException {
        ClientCallBack callBack = new ClientCallBack();
        BaseRequest request = protocol.buildRequest(data);
        responses.put(request.getMessageId(), callBack);
        send(request);
        BaseResponse response = callBack.get(request.getTimeout(), TimeUnit.MILLISECONDS);
        return response.parse();
    }

    @Override
    public void write(Object data) throws RemoteException {
        BaseHeader request = protocol.buildCommon(data);
        send(request);
    }

    public void receive(BaseHeader header){
        if (header instanceof BaseRequest) {
            handleRequest((BaseRequest) header);
        } else if (header instanceof BaseResponse) {
            handleResponse((BaseResponse) header);
        } else if (header instanceof BaseCommon) {
            handleCommon((BaseCommon) header);
        } else {
            LOGGER.error("[CONFIG] drop unknown message from " + getConnection().getRemoteAddress());
        }
    }

    private void handleCommon(BaseCommon common) {
        processor.handleCommon(common, getConnection());
    }

    private void handleRequest(BaseRequest request) {
        processor.handleRequest(request, getConnection());
    }

    private void handleResponse(BaseResponse response) {
        ClientCallBack callBack = responses.remove(response.getMessageId());
        if (callBack != null) {
            callBack.complete(response);
        }
    }

    private void send(BaseHeader header) throws RemoteException {
        getConnection().write(header);
    }

    public Connection getConnection() {
        return connection;
    }
}
