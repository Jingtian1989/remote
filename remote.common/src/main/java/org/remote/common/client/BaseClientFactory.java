package org.remote.common.client;

import org.remote.common.protocol.PacketService;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.protocol.ProtocolSetting;
import org.remote.common.service.ProcessorRegistrar;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public abstract class BaseClientFactory implements ClientFactory{

    private Map<SocketAddress, Client> clients;

    public BaseClientFactory() {
        this.clients = new ConcurrentHashMap<SocketAddress, Client>();
    }

    @Override
    public Client build(String host, int port, ProcessorRegistrar registrar) throws ConnectException {
        //default packet service
        ProtocolService protocol = new PacketService();
        return build(host, port, protocol, registrar);
    }

    @Override
    public Client build(String host, int port, ProtocolService protocol, ProcessorRegistrar registrar) throws ConnectException{
        InetSocketAddress address = new InetSocketAddress(host, port);
        Client client = clients.get(address);
        if (client != null) {
            return client;
        }
        client = connect(address, ProtocolSetting.DEFAULT_TIMEOUT, protocol, registrar);
        clients.put(address, client);
        return client;
    }

    public abstract Client connect(SocketAddress address, int timeout, ProtocolService protocol, ProcessorRegistrar processor) throws ConnectException;

    @Override
    public Client query(SocketAddress address) {
        return clients.get(address);
    }
}
