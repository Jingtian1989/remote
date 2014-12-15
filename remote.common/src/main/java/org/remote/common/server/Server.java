package org.remote.common.server;

import org.remote.common.service.ProcessorRegistrar;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public abstract class Server {

    private ProcessorRegistrar registrar;
    private String host;
    private int port;

    public Server(String host, int port, ProcessorRegistrar registrar) {
        this.registrar = registrar;
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public abstract void start();

    public abstract void stop();

}
