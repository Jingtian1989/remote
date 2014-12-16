package org.remote.common.client;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ClientCallBack {

    public void handleResponse(Object data);

    public void handleException(Exception e);

}
