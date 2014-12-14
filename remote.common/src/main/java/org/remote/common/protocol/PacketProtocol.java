package org.remote.common.protocol;

import org.remote.common.buffer.ByteBufferWrapper;
import org.remote.common.domain.BaseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by jingtian.zjt on 2014/12/10.
 */
/*
 * Packet codecs.
 * 请求：
 * 字节数       描述
 * 1           协议
 * 1           版本
 * 1           请求
 * 1         序列化方式
 * 8          请求ID
 * 4          超时时间
 * 4         数据包长度
 * 不等        数据包
 *
 * 响应：
 * 字节数      描述
 * 1          协议
 * 1          版本
 * 1          响应
 * 1        序列化方式
 * 1        状态code
 * 3        保留字节
 * 8        对应的请求ID
 * 4        数据包长度
 * 不等      数据包
 *
 *
 * 通用：
 * 字节数      描述
 * 1          协议
 * 1          版本
 * 1          通用
 * 1         序列化方式
 * 8          消息ID
 * 4         数据包长度
 * 不等        数据包
 */
public class PacketProtocol implements Protocol{

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketProtocol.class);

    public static final byte PACKET_PROTOCOL = 0x3e;

    private static final int REQUEST_HEADER_LEN = 16;
    private static final int RESPONSE_HEADER_LEN = 20;
    private static final int COMMON_HEADER_LEN = 16;

    private static final byte VERSION = (byte)0x01;
    private static final byte REQUEST = (byte)0x00;
    private static final byte RESPONSE = (byte)0x01;
    private static final byte COMMON = (byte)0x02;

    public void encode(BaseHeader message, ByteBufferWrapper wrapper) throws RuntimeException {
        if (message instanceof PacketRequest) {
            encodeRequest((PacketRequest)message, wrapper);
        } else if (message instanceof PacketResponse) {
            encodeResponse((PacketResponse) message, wrapper);
        } else if (message instanceof PacketCommon) {
            encodeCommon((PacketCommon) message, wrapper);
        } else {
            LOGGER.error("[REMOTE] unsupported message type.");
            throw new RuntimeException("unsupported message type.");
        }
    }

    private void encodeCommon(PacketCommon common, ByteBufferWrapper wrapper) {
        int capacity = COMMON_HEADER_LEN + common.getPacket().length;
        wrapper.init(capacity);
        wrapper.writeByte(PACKET_PROTOCOL);
        wrapper.writeByte(VERSION);
        wrapper.writeByte(COMMON);
        wrapper.writeByte(common.getCodecType());
        wrapper.writeLong(common.getMessageId());
        wrapper.writeInt(common.getPacket().length);
        wrapper.writeBytes(common.getPacket());
    }

    private void encodeRequest(PacketRequest request, ByteBufferWrapper wrapper) {
        int capacity = REQUEST_HEADER_LEN + request.getPacket().length;
        wrapper.init(capacity);
        wrapper.writeByte(PACKET_PROTOCOL);
        wrapper.writeByte(VERSION);
        wrapper.writeByte(REQUEST);
        wrapper.writeByte(request.getCodecType());
        wrapper.writeLong(request.getMessageId());
        wrapper.writeInt(request.getTimeout());
        wrapper.writeInt(request.getPacket().length);
        wrapper.writeBytes(request.getPacket());
    }

    private void encodeResponse(PacketResponse response, ByteBufferWrapper wrapper) {
        int capacity = RESPONSE_HEADER_LEN + response.getPacket().length;
        wrapper.init(capacity);
        wrapper.writeByte(PACKET_PROTOCOL);
        wrapper.writeByte(VERSION);
        wrapper.writeByte(RESPONSE);
        wrapper.writeByte(response.getCodecType());
        wrapper.writeByte(response.getStatus().getCode());
        wrapper.writeByte((byte)0);
        wrapper.writeByte((byte)0);
        wrapper.writeByte((byte)0);
        wrapper.writeLong(response.getMessageId());
        wrapper.writeInt(response.getPacket().length);
        wrapper.writeBytes(response.getPacket());
    }


    public BaseHeader decode(ByteBufferWrapper wrapper, int origin) throws RuntimeException {

        if (wrapper.readableBytes() < 2) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte version = wrapper.readByte();
        if (version == VERSION) {
            byte type = wrapper.readByte();
            if (type == REQUEST) {
                return decodeRequest(wrapper, origin);
            } else if (type == RESPONSE) {
                return decodeResponse(wrapper, origin);
            } else if (type == COMMON) {
                return decodeCommon(wrapper, origin);
            } else {
                LOGGER.error("[REMOTE] message type " + type + " is not supported.");
                throw new RuntimeException("message type " + type + " is not supported");
            }
        } else {
            LOGGER.error("[REMOTE] unsupported codecs version " + version);
            throw new RuntimeException("codecs version " + version + "is not supported");
        }
    }

    private BaseHeader decodeCommon(ByteBufferWrapper wrapper, int origin) {
        if (wrapper.readableBytes() < COMMON_HEADER_LEN - 3) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte codecType = wrapper.readByte();
        long commonId = wrapper.readLong();
        int len = wrapper.readInt();
        if (wrapper.readableBytes() < len) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte[] body = new byte[len];
        wrapper.readBytes(body);
        return new PacketCommon(commonId, codecType, body);
    }

    public BaseHeader decodeRequest(ByteBufferWrapper wrapper, int origin) {
        if (wrapper.readableBytes() < REQUEST_HEADER_LEN - 3) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte codecType = wrapper.readByte();
        long requestId = wrapper.readLong();
        int timeout = wrapper.readInt();
        int len = wrapper.readInt();
        if (wrapper.readableBytes() < len) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte[] body = new byte[len];
        wrapper.readBytes(body);
        return new PacketRequest(requestId, timeout, codecType, body);
    }

    public BaseHeader decodeResponse(ByteBufferWrapper wrapper, int origin) {
        if (wrapper.readableBytes() < RESPONSE_HEADER_LEN - 3) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte codecType = wrapper.readByte();
        byte status = wrapper.readByte();
        wrapper.readByte();
        wrapper.readByte();
        wrapper.readByte();
        long requestId = wrapper.readLong();
        int len  = wrapper.readInt();
        if (wrapper.readableBytes() < len) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte[] body = new byte[len];
        wrapper.readBytes(body);
        return new PacketResponse(requestId, codecType, ProtocolStatus.formatCode(status), body);
    }
}
