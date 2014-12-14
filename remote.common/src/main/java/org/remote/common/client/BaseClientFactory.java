package org.remote.common.client;

import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.PacketService;
import org.remote.common.protocol.ProtocolService;
import org.remote.common.protocol.ProtocolSetting;
import org.remote.common.service.ProcessorService;

import java.net.InetAddress;
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
    public Client build(String host, int port, ProcessorService processor) throws RemoteException {
        //default packet service
        ProtocolService protocol = new PacketService();
        return build(host, port, protocol, processor);
    }

    @Override
    public Client build(String host, int port, ProtocolService protocol, ProcessorService processor) throws RemoteException{
        InetSocketAddress address = new InetSocketAddress(host, port);
        Client client = clients.get(address);
        if (client != null) {
            throw new RemoteException("[CONFIG] existed getConnection to " + address);
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
