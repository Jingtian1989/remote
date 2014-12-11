package org.remote.common.client;

import org.remote.common.domain.BaseResponse;
import org.remote.common.exception.RemoteCode;
import org.remote.common.exception.RemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class ClientCallBack {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCallBack.class);

    private BaseResponse response;
    private final CountDownLatch latch = new CountDownLatch(1);

    public BaseResponse get(long timeout, TimeUnit unit) throws RemoteException{
        try {
            if (!latch.await(timeout, unit)) {
                throw new RemoteException(RemoteCode.REMOTE_CLIENT_CONN_TIMEOUT, "request timeout.");
            }
        } catch (InterruptedException e) {
            LOGGER.error("[REMOTE] wait response failed. exception:", e);
            throw new RemoteException(RemoteCode.REMOTE_CLIENT_CONN_FAILED, "request interrupted.");
        }
        return response;
    }

    public void complete(final BaseResponse response) {
        synchronized (this) {
            if (this.response == null) {
                this.response = response;
            } else {
                return;
            }
        }
        latch.countDown();
    }
}
