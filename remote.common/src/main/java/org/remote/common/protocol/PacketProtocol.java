package org.remote.common.protocol;

import org.remote.common.buffer.ByteBufferWrapper;
import org.remote.common.domain.BaseHeader;
import org.remote.common.exception.CodecsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by jingtian.zjt on 2014/12/10.
 */
/*
 * Packet protocol.
 * request
 * bytes       fields
 * 1           protocol
 * 1           version
 * 1           request
 * 1           codecs type
 * 8           messageID
 * 4           timeout
 * 4           data len
 * len         data
 *
 * response
 * bytes      fields
 * 1          protocol
 * 1          version
 * 1          response
 * 1          codecs type
 * 1          status code
 * 3          keep
 * 8          messageID
 * 4          data len
 * len        data
 *
 *
 */
public class PacketProtocol implements Protocol{

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketProtocol.class);

    public static final byte PACKET_PROTOCOL = 0x3e;

    private static final int REQUEST_HEADER_LEN = 16;
    private static final int RESPONSE_HEADER_LEN = 20;

    private static final byte VERSION = (byte)0x01;
    private static final byte REQUEST = (byte)0x00;
    private static final byte RESPONSE = (byte)0x01;

    public void encode(BaseHeader message, ByteBufferWrapper wrapper) throws RuntimeException {
        if (message instanceof PacketRequest) {
            encodeRequest((PacketRequest)message, wrapper);
        } else if (message instanceof PacketResponse) {
            encodeResponse((PacketResponse) message, wrapper);
        } else {
            LOGGER.error("[REMOTE] unsupported message type.");
            throw new RuntimeException("unsupported message type.");
        }
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
            } else {
                LOGGER.error("[REMOTE] message type " + type + " is not supported.");
                throw new RuntimeException("message type " + type + " is not supported");
            }
        } else {
            LOGGER.error("[REMOTE] unsupported version " + version);
            throw new RuntimeException("unsupported version " + version);
        }
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
