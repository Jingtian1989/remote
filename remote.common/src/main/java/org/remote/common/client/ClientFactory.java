package org.remote.common.client;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ClientFactory {

    public Client get(SocketAddress address, boolean connect) throws Exception;

}
