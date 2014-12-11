package org.remote.common.server;

import org.remote.common.domain.BaseResponse;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public interface Connection {

    public SocketAddress getLocalAddress();

    public SocketAddress getRemoteAddress();

    public void write(BaseResponse response);
}
