package org.remote.common.client;

import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.protocol.ProtocolSetting;
import org.remote.common.service.ProcessorService;

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
    public Client build(SocketAddress address, ProtocolService protocol, ProcessorService processor) throws RemoteException{
        Client client = clients.get(address);
        if (client != null) {
            throw new RemoteException("[CONFIG] existed connection to " + address);
        }
        client = connect(address, ProtocolSetting.DEFAULT_TIMEOUT, protocol, processor);
        clients.put(address, client);
        return client;
    }

    public abstract Client connect(SocketAddress address, int timeout, ProtocolService protocol, ProcessorService processor) throws RemoteException;

    @Override
    public Client query(SocketAddress address) {
        return clients.get(address);
    }
}
