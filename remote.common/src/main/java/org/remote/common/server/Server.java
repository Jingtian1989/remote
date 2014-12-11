package org.remote.common.server;

/**
 * Created by jingtian.zjt on 2014/12/10.
 */
public abstract class Server {


    private String host;
    private int port;

    public Server(String host, int port) {

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
