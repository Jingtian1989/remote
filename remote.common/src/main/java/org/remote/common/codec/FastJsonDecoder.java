package org.remote.common.codec;

import com.alibaba.fastjson.JSON;
import org.remote.common.protocol.ProtocolSetting;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class FastJsonDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes) {
        return JSON.parse(new String(bytes, ProtocolSetting.DEFAULT_CHARSET));
    }
}
