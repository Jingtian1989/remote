package org.remote.common.client;

import org.remote.common.domain.BaseRequest;
import org.remote.common.domain.BaseResponse;
import org.remote.common.protocol.ProtocolService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public abstract class BaseClient implements Client {

    private final ConcurrentHashMap<Long, ClientCallBack> responses =
            new ConcurrentHashMap<Long, ClientCallBack>();
    private ProtocolService protocol;

    public BaseClient(ProtocolService protocol) {
        this.protocol = protocol;
    }

    @Override
    public Object syncInvoke(Object data) throws Exception {
        ClientCallBack callBack = new ClientCallBack();
        BaseRequest request = protocol.request(data);
        responses.put(request.getRequestID(), callBack);
        send(request);
        BaseResponse response = callBack.get(request.getTimeout(), TimeUnit.MILLISECONDS);
        return response.parse();
    }

    public void complete(BaseResponse response){
        ClientCallBack callBack = responses.remove(response.getRequestID());
        if (callBack != null) {
            callBack.complete(response);
        }
    }
    public abstract void send(BaseRequest request) throws Exception;
}
