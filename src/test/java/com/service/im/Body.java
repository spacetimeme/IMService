package com.service.im;

import com.dream.socket.codec.Message;

public class Body extends Message {

    public short type;

    public Body(byte[] body) {

    }

    public byte[] getBody() {
        return new byte[0];
    }
}
