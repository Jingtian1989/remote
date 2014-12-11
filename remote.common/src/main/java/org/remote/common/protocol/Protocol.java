package org.remote.common.protocol;

import org.remote.common.buffer.ByteBufferWrapper;
import org.remote.common.domain.BaseHeader;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public interface Protocol {

    public void encode(BaseHeader message, ByteBufferWrapper wrapper) throws Exception;

    public BaseHeader decode(ByteBufferWrapper wrapper, int origin) throws Exception;
}
