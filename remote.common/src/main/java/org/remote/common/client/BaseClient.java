package org.remote.common.client;

import org.remote.common.domain.BaseRequest;
import org.remote.common.exception.CodecsException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.server.Connection;
import org.remote.common.service.ProcessorRegistrar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    public void invoke(Object data, ClientCallBack callBack){
        try {
            BaseRequest request = protocol.buildRequest(data);
            connection.setCallBack(callBack);
            connection.write(request);
        } catch (Exception e) {
            callBack.handleException(e);
        }
    }

    @Override
    public Object invoke(Object data) throws CodecsException, InterruptedException, TimeoutException {
        ClientTimer timer = new ClientTimer();
        SynCallBack synCallBack = new SynCallBack(timer);
        connection.setCallBack(synCallBack);
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

    private static class SynCallBack implements org.remote.common.client.ClientCallBack {

        private ClientTimer timer;

        public SynCallBack(ClientTimer timer) {
            this.timer = timer;
        }

        @Override
        public void handleResponse(Object data) {
            timer.arrive(data);
        }

        @Override
        public void handleException(Exception e) {
        }
    }


    private static class ClientTimer {

        private Object data;
        private CountDownLatch latch;
        public ClientTimer() {
            this.latch = new CountDownLatch(1);
        }

        public Object get(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException{
            if (!latch.await(timeout, unit)) {
                throw new TimeoutException();
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
