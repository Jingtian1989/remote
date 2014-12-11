package org.remote.common.protocol;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class ProtocolFactory {

    private static final Protocol[] protocols = new Protocol[256];
    private static final ProtocolService[] services = new ProtocolService[256];
    private static final ProtocolFactory instance = new ProtocolFactory();
    public static ProtocolFactory getInstance() {
        return instance;
    }

    private ProtocolFactory(){}

    static {
        registerProtocol(PacketProtocol.PACKET_PROTOCOL, new PacketProtocol(), new PacketService());
    }

    public static void registerProtocol(int type, Protocol protocol, ProtocolService service) {
        type = (type < 0 ? type + 128 : type);
        if (protocols[type] != null) {
            throw new RuntimeException("protocol handler has been registered.");
        }
        protocols[type] = protocol;
        services[type] = service;
    }

    public Protocol getProtocol(int type) {
        type = (type < 0 ? type + 128 : type);
        return protocols[type];
    }

    public ProtocolService getProtocolService(int type) {
        type = (type < 0 ? type + 128 : type);
        return services[type];
    }

}
