package com.service.im.protocol;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;

public abstract class Body<T> implements Protocol {

    /**
     * 当前消息的管道
     */
    public Channel channel;

    public int version;

    private byte[] body;

    public Body(byte[] body) {
        this.body = body;
    }

    public byte[] getArray() {
        return body;
    }

    public int getLength() {
        return body.length;
    }

    public abstract T decode(ByteBuffer buffer);

    public abstract ByteBuffer encode(T t);
}
