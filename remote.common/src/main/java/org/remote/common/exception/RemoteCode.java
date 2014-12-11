package org.remote.common.exception;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public enum RemoteCode {

    REMOTE_CODECS_DECODE_FAILED(0x00, "REMOTE_CODECS_DECODE_FAILED"),
    REMOTE_CODECS_ENCODE_FAILED(0x01, "REMOTE_CODECS_ENCODE_FAILED"),
    REMOTE_CLIENT_CONN_TIMEOUT(0x02, "REMOTE_CLIENT_CONN_TIMEOUT"),
    REMOTE_CLIENT_CONN_FAILED(0x03, "REMOTE_CLIENT_CONN_FAILED"),
    REMOTE_SERVER_INVOKE_FAILED(0x04, "REMOTE_SERVER_INVOKE_FAILED");

    private int code;
    private String value;
    private RemoteCode(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
