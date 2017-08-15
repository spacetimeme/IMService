package com.service.im.work;

import com.google.protobuf.ByteString;
import com.service.im.protobuf.Protobuf;
import com.service.im.protobuf.Type;
import com.service.im.protocol.Packet;
import com.service.im.session.Session;
import io.netty.channel.Channel;

public class MessageWork {

    public void doACK(Protobuf.Body body) {

    }

    public boolean doLogin(Channel channel, Protobuf.Body body) {
        Protobuf.ACK ack;
        try {
            Protobuf.Login login = Protobuf.Login.parseFrom(body.getContent());
            ack = Protobuf.ACK.newBuilder().setId(body.getId()).setCode(0).setMessage("登陆成功").build();
            channel.writeAndFlush(packet(getBody(body.getId(), Type.BODY_ACK, 0, ack.toByteString())));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ack = Protobuf.ACK.newBuilder().setId(body.getId()).setCode(0).setMessage("登陆失败").build();
            channel.writeAndFlush(packet(getBody(body.getId(), Type.BODY_ACK, 0, ack.toByteString())));
        }
        return false;
    }

    public void doMessage(Channel channel, Protobuf.Body body) {
        Protobuf.ACK ack;
        try {
            Protobuf.Message message = Protobuf.Message.parseFrom(body.getContent());
            System.out.println(message.getText());
            ack = Protobuf.ACK.newBuilder().setId(body.getId()).setCode(0).build();
            channel.writeAndFlush(packet(getBody(body.getId(), Type.BODY_ACK, 0, ack.toByteString())));
            channel = Session.ONLINE_CHANNEL.get(message.getReceiver());
            if(channel != null){
                channel.writeAndFlush(packet(getBody(body.getId(), Type.BODY_MESSAGE, body.getSender(), body.getContent())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Packet packet(byte[] body) {
        return new Packet(body);
    }

    private byte[] getBody(String id, int type, long sender, ByteString content) {
        return Protobuf.Body.newBuilder().setId(id).setType(type).setSender(sender).setContent(content).build().toByteArray();
    }
}
