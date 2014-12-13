package org.remote.common.client;


import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.service.ProcessorService;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ClientFactory {

    public Client build(SocketAddress address, ProtocolService protocol, ProcessorService processor) throws RemoteException;

    public Client query(SocketAddress address) throws RemoteException;

}
