package com.service.im.protocol;

import com.service.im.session.Session;
import io.netty.channel.Channel;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Body implements Protocol {

    private static final Map<Integer, Class<?>> BODY_TYPE_MAP = new HashMap<>();
    private Channel channel;
    private Session session;
    private ByteBuffer buffer;
    private int type;

    public Body(Channel channel, byte[] body) {
        this.channel = channel;
        this.buffer = ByteBuffer.wrap(body);
        this.session = channel.attr(Session.KEY).get();
        this.buffer.flip();
        this.type = this.buffer.getInt();
    }

    public static void register(int type, Class<?> clazz) {
        BODY_TYPE_MAP.put(type, clazz);
    }

    public Channel getChannel() {
        return channel;
    }

    public Session getSession() {
        return session;
    }

    public byte[] getArray() {
        return buffer.array();
    }

    public int getLength() {
        return buffer.capacity();
    }

}
