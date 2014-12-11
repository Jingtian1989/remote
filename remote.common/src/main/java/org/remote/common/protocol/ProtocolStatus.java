package org.remote.common.protocol;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public enum ProtocolStatus {

    OK((byte)0),
    ERROR((byte)1);

    private byte code;
    private ProtocolStatus(byte code) {
        this.code = code;
    }

    public static ProtocolStatus formatCode(byte code) throws IllegalArgumentException{
        for (ProtocolStatus status : ProtocolStatus.values()) {
            if (code == status.code) {
                return status;
            }
        }
        throw new IllegalArgumentException("unknown status code.");
    }

    public byte getCode() {
        return code;
    }
}
