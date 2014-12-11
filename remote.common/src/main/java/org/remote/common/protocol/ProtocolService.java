package org.remote.common.protocol;

import org.remote.common.domain.BaseRequest;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface ProtocolService {

    public BaseRequest request(Object data) throws Exception;
}
