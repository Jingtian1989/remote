package org.remote.common.client;

import org.remote.common.domain.BaseCommon;
import org.remote.common.domain.BaseHeader;
import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteCode;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public abstract class BaseClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseClient.class);

    private Map<Long, ClientTimer> responses;
    private Connection connection;
    private ProtocolService protocol;
    private ProcessorService processor;


    public BaseClient(Connection connection, ProtocolService protocol, ProcessorService processor) {
        this.connection = connection;
        this.protocol = protocol;
        this.processor = processor;
        this.responses = new ConcurrentHashMap<Long, ClientTimer>();
    }

    @Override
    public Object invoke(Object data) throws RemoteException {
        ClientTimer timer = new ClientTimer();
        BaseRequest request = protocol.buildRequest(data);
        responses.put(request.getMessageId(), timer);
        send(request);
        BaseResponse response = (BaseResponse) timer.get(request.getTimeout(), TimeUnit.MILLISECONDS);
        return response.parse();
    }


    @Override
    public void invoke(Object data, final ClientCallBack callBack) throws RemoteException {
        final ClientTimer timer = new ClientTimer();
        final BaseRequest request = protocol.buildRequest(data);
        Executor executor = processor.getExecutor(request);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BaseResponse response = (BaseResponse)timer.get(request.getTimeout(), TimeUnit.MILLISECONDS);
                    Object data = response.parse();
                    callBack.handle(data);
                } catch (RemoteException e) {
                    callBack.exception(e);
                }
            }
        });
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
            LOGGER.error("[CONFIG] drop unknown message from " + connection().getRemoteAddress());
        }
    }

    private void handleCommon(BaseCommon common) {
        processor.handleCommon(common, connection());
    }

    private void handleRequest(BaseRequest request) {
        processor.handleRequest(request, connection());
    }

    private void handleResponse(BaseResponse response) {
        ClientTimer timer = responses.remove(response.getMessageId());
        if (timer != null) {
            timer.arrive(response);
        }
    }

    private void send(BaseHeader header) throws RemoteException {
        connection().write(header);
    }

    public Connection connection() {
        return connection;
    }

    private static class ClientTimer {

        private Object data;
        private CountDownLatch latch;
        public ClientTimer() {
            this.latch = new CountDownLatch(1);
        }

        public Object get(long timeout, TimeUnit unit) throws RemoteException{
            try {
                if (!latch.await(timeout, unit)) {
                    throw new RemoteException(RemoteCode.REMOTE_CLIENT_CONN_TIMEOUT, "request timeout.");
                }
            } catch (InterruptedException e) {
                throw new RemoteException(RemoteCode.REMOTE_CLIENT_CONN_FAILED, "request interrupted.");
            }
            return data;
        }

        public void arrive(Object data) {
            if (this.data == null) {
                this.data = data;
            } else {
                return;
            }
            latch.countDown();
        }
    }
}
