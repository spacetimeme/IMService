package com.service.im.protocol;

import com.service.im.session.Session;
import io.netty.channel.Channel;

import java.util.UUID;

public class Body implements Protocol {

    public static final int TYPE_STRING = 1000;
    private Channel channel;
    private Session session;

    private String id;
    private int type;
    private int sender;
    private int recipient;
    private byte[] body;

    public Body(Channel channel, byte[] id, int type, int sender, int recipient, byte[] body) {
        this.channel = channel;
        this.id = new String(id);
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
    }

    public Body(int type, int sender, int recipient, byte[] body) {
        this.id = id();
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
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

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getSender() {
        return sender;
    }

    public int getRecipient() {
        return recipient;
    }

    public byte[] getBody() {
        return body;
    }

    private static String id(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
