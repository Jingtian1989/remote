package org.remote.common.client;

import org.remote.common.exception.RemoteException;
import org.remote.common.protocol.ProtocolSetting;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public abstract class BaseClientFactory implements ClientFactory{
    private final ConcurrentHashMap<SocketAddress, Client> clients =
            new ConcurrentHashMap<SocketAddress, Client>();

    @Override
    public Client get(SocketAddress address, boolean connect) throws RemoteException {
        Client client = clients.get(address);
        if (client == null && !connect) {
            return null;
        }
        if (client == null || !client.isConnected()) {
            synchronized (this) {
                client = clients.get(address);
                if (client == null) {
                    client = connect(address, ProtocolSetting.DEFAULT_TIMEOUT);
                    clients.put(address, client);
                } else if (!client.isConnected()){
                    clients.remove(address, client);
                    client = connect(address, ProtocolSetting.DEFAULT_TIMEOUT);
                    clients.put(address, client);
                }
            }
        }
        return client;
    }

    public abstract Client connect(SocketAddress address, int timeout) throws RemoteException;
}
