package org.remote.common.protocol;

import org.remote.common.codec.Codecs;

import java.nio.charset.Charset;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class ProtocolSetting {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final int DEFAULT_TIMEOUT = 3000;

    public static final int DEFAULT_MAX_IDLE = 3000;

    public static final int DEFAULT_CODECS = Codecs.JAVA_CODEC;
}
