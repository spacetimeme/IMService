package com.service.im.message;

public class StringBody extends MessageBody {

    private String string;

    public StringBody(String string, int sender, int recipient) {
        super(TYPE_STRING, sender, recipient);
        this.string = string;
    }

    @Override
    public byte[] getBytes() {
        if(string != null){
            return string.getBytes();
        }
        return new byte[0];
    }
}
