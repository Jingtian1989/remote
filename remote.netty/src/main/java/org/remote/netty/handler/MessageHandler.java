package org.remote.netty.handler;

import org.remote.common.client.ClientCallBack;
import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolFactory;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.server.Connection;
import org.remote.common.service.Processor;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.common.service.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jingtian.zjt on 2014/12/15.
 */
public class MessageHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    private final ProcessorRegistrar registrar;
    private final Connection connection;
    private final BaseHeader header;
    private final long dispatchTime = System.currentTimeMillis();

    public MessageHandler(BaseHeader header, Connection connection, ProcessorRegistrar registrar) {
        this.header = header;
        this.connection = connection;
        this.registrar = registrar;
    }

    @Override
    public void run() {
        long begin = System.currentTimeMillis();
        long pending = begin - dispatchTime;
        if (header instanceof BaseRequest) {
            BaseRequest request = (BaseRequest) header;
            int clientTimeout = request.getTimeout();
            if (clientTimeout > 0 && pending >= clientTimeout) {
                LOGGER.error("[REMOTE] drop timeout client " + connection.getRemoteAddress() + " for pending time " + pending);
                return;
            }
            handleRequest(request, connection);
        } else if (header instanceof BaseResponse) {
            BaseResponse response = (BaseResponse) header;
            handleResponse(response, connection);
        }
    }

    private void handleResponse(BaseResponse response, Connection connection) {
        Object data = null;
        try {
            data = response.parse();
        } catch (RemoteException e) {
            LOGGER.error("[REMOTE] parse response failed. exception:", e);
            return;
        }
        try {
            ClientCallBack callBack = connection.getCallBack();
            if (callBack != null) {
                callBack.handleResponse(data);
            }
        } catch (Exception e) {
            LOGGER.error("[REMOTE] unexpected application exception when handle response message. exception:", e);
        }
    }

    private void handleRequest(BaseRequest request, Connection connection) {
        Object data = null;
        try {
            data = request.parse();
        } catch (RemoteException e){
            LOGGER.error("[REMOTE] parse request failed. exception:", e);
            BaseResponse response = request.error("parse request failed.");
            connection.write(response);
        }
        Processor processor = registrar.getProcessor(data);
        if (processor == null) {
            LOGGER.error("[REMOTE] unsupported message " + data.getClass().getCanonicalName() + " from " + connection.getRemoteAddress());
            BaseResponse response = request.error("unsupported message " + data.getClass().getCanonicalName());
            connection.write(response);
            return;
        }
        try {
            ProtocolService protocol = ProtocolFactory.getInstance().getProtocolService(request.getProtocolType());
            processor.handle(data, new Writer(connection, protocol, request));
        } catch (Exception e) {
            LOGGER.error("[REMOTE] unexpected application exception when handle request. exception:", e);
            BaseResponse response = request.error("unexpected application exception");
            connection.write(response);
        }
    }
}
