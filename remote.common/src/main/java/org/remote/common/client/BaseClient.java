package org.remote.common.client;

import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteCode;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorRegistrar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public abstract class BaseClient implements Client {

    private Connection connection;
    private ProtocolService protocol;
    private ProcessorRegistrar registrar;

    public BaseClient(Connection connection, ProtocolService protocol, ProcessorRegistrar registrar) {
        this.connection = connection;
        this.protocol = protocol;
        this.registrar = registrar;
    }

    @Override
    public void invoke(Object data, CallBack callBack) throws RemoteException {
        BaseRequest request = protocol.buildRequest(data);
        connection.setCallBack(callBack);
        connection.write(request);
    }

    @Override
    public Object invoke(Object data) throws RemoteException {
        ClientTimer timer = new ClientTimer();
        ClientCallBack clientCallBack = new ClientCallBack(timer);
        connection.setCallBack(clientCallBack);
        BaseRequest request = protocol.buildRequest(data);
        connection.write(request);
        return timer.get(request.getTimeout(), TimeUnit.MILLISECONDS);
    }

    public ProcessorRegistrar getRegistrar() {
        return registrar;
    }

    public Connection getConnection() {
        return connection;
    }

    private static class ClientCallBack implements CallBack {

        private ClientTimer timer;

        public ClientCallBack(ClientTimer timer) {
            this.timer = timer;
        }

        @Override
        public void handleResponse(Object data) {
            timer.arrive(data);
        }

        @Override
        public void handleException(RemoteException e) {
        }
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
