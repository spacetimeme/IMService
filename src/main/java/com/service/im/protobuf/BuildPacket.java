package com.service.im.protobuf;

import com.google.protobuf.ByteString;
import com.service.im.protocol.Packet;

public class BuildPacket {

    public static Packet buildBody(String id, BodyType type, int sender, ByteString content) {
        Protobuf.Body.Builder builder = Protobuf.Body.newBuilder();
        if (id != null) {
            builder.setId(id);
        }
        builder.setType(type.getTypeCode());
        builder.setSender(sender);
        if (content != null) {
            builder.setContent(content);
        }
        return new Packet(builder.build().toByteArray());
    }

    public static ByteString buildResponse(int code, ByteString data) {
        Protobuf.Response.Builder builder = Protobuf.Response.newBuilder();
        builder.setCode(code);
        if (data != null) {
            builder.setData(data);
        }
        return builder.build().toByteString();
    }

    public static ByteString buildMessage(int receiver, MessageType type, ByteString content) {
        Protobuf.Message.Builder builder = Protobuf.Message.newBuilder();
        builder.setReceiver(receiver);
        builder.setType(type.getTypeCode());
        if (content != null) {
            builder.setContent(content);
        }
        return builder.build().toByteString();
    }

}
