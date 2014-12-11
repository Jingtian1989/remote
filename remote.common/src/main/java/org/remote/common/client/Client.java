package org.remote.common.client;


/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface Client {

    public Object syncInvoke(Object request) throws Exception;

    public boolean isConnected();
}
