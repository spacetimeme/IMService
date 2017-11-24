package com.service.im;

public class TextBody extends Body {

    private String string;

    public TextBody(byte[] body) {
        super(body);
        string = new String(body);
    }

    public String getString() {
        return string;
    }

    @Override
    public byte[] getBody(){
        return string.getBytes();
    }
}
