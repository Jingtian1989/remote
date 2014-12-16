package org.remote.common.client;


import org.remote.common.protocol.ProtocolService;
import org.remote.common.service.ProcessorRegistrar;

import java.net.ConnectException;
import java.net.SocketAddress;


/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ClientFactory {

    public Client build(String host, int port, ProcessorRegistrar registrar) throws ConnectException;

    public Client build(String host, int port, ProtocolService protocol, ProcessorRegistrar registrar) throws ConnectException;

    public Client query(SocketAddress address);

    public Client remove(SocketAddress address);

}
