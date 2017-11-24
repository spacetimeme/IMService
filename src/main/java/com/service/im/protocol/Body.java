package com.service.im.protocol;

import com.service.im.session.Session;
import io.netty.channel.Channel;

public class Body implements Protocol {

    private Channel channel;
    private Session session;
    private int type;
    private byte[] body;

    public Body(Channel channel, int type, byte[] body) {
        this.channel = channel;
        this.type = type;
        this.body = body;
    }

    public Body(int type, byte[] body) {
        this.type = type;
        this.body = body;
    }

    public Channel getChannel() {
        return channel;
    }

    public Session getSession() {
        if (channel == null) {
            return null;
        }
        if (session == null) {
            session = channel.attr(Session.KEY).get();
        }
        return session;
    }

    public int getType() {
        return type;
    }

    public byte[] getBody() {
        return body;
    }
}
