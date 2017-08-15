package com.service.im.protobuf;

import com.google.protobuf.ByteString;
import com.service.im.protocol.Packet;

public class BuildPacket {

    public static Packet buildBody(String id, int type, long sender, ByteString content) {
        Protobuf.Body.Builder builder = Protobuf.Body.newBuilder();
        if(id != null){
            builder.setId(id);
        }
        builder.setType(type);
        builder.setSender(sender);
        if (content != null) {
            builder.setContent(content);
        }
        return new Packet(builder.build().toByteArray());
    }

    public static ByteString buildResponse(int type, int code, String data) {
        Protobuf.Response.Builder builder = Protobuf.Response.newBuilder();
        builder.setType(type);
        builder.setCode(code);
        if (data != null) {
            builder.setData(data);
        }
        return builder.build().toByteString();
    }

    public static ByteString buildMessage(long receiver, int type, String text) {
        Protobuf.Message.Builder builder = Protobuf.Message.newBuilder();
        builder.setReceiver(receiver);
        builder.setType(type);
        if (text != null) {
            builder.setText(text);
        }
        return builder.build().toByteString();
    }

}
