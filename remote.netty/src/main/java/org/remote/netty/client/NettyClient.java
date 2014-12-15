package org.remote.netty.client;

import org.remote.common.client.BaseClient;
import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.netty.server.NettyConnection;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class NettyClient extends BaseClient {

    public NettyClient(NettyConnection connection, ProtocolService protocol, ProcessorRegistrar processor) {
        super(connection, protocol, processor);
    }
}
