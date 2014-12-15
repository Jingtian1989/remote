package org.remote.common.server;

import org.remote.common.client.CallBack;
import org.remote.common.domain.BaseHeader;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public interface Connection {

    public SocketAddress getLocalAddress();

    public SocketAddress getRemoteAddress();

    public void write(BaseHeader header);

    public boolean isConnected();

    public void setCallBack(CallBack callBack);

    public CallBack getCallBack();
}
