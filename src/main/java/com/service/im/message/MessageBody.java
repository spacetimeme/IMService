package com.service.im.message;

import com.service.im.protocol.Body;
import io.netty.channel.Channel;

public abstract class MessageBody extends Body {

    public MessageBody(Channel channel, byte[] id, int type, int sender, int recipient) {
        super(channel, id, type, sender, recipient, null);
    }

    public MessageBody(int type, int sender, int recipient) {
        super(type, sender, recipient, null);
    }

    @Override
    public final byte[] getBody() {
        return getBytes();
    }

    public abstract byte[] getBytes();
}
