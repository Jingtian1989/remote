package org.remote.common.domain;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseRequest extends BaseHeader {

    private final int timeout;
    public BaseRequest(int protocolType, long id, int timeout, byte codecType) {
        super(protocolType, id, codecType);
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }


    public abstract BaseResponse error(String msg);

}
