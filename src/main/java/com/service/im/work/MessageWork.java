package com.service.im.work;

import com.google.protobuf.ByteString;
import com.service.im.protobuf.BuildPacket;
import com.service.im.protobuf.Protobuf;
import com.service.im.protobuf.Type;
import com.service.im.protocol.Packet;
import com.service.im.session.Session;
import io.netty.channel.Channel;

public class MessageWork {

    public boolean login(Channel channel, String bodyId, Protobuf.Login login) {
        try {
            ByteString bs = BuildPacket.buildResponse(Type.BODY_LOGIN, 0, "登陆成功");
            Packet packet = BuildPacket.buildBody(bodyId, Type.BODY_LOGIN, 0, bs);
            channel.writeAndFlush(packet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ByteString bs = BuildPacket.buildResponse(Type.BODY_LOGIN, -1, "登陆失败");
            Packet packet = BuildPacket.buildBody(bodyId, Type.BODY_LOGIN, 0, bs);
            channel.writeAndFlush(packet);
        }
        return false;
    }

    public void message(Channel channel, Protobuf.Body body) {
        try {
            Packet packet = BuildPacket.buildBody(body.getId(), Type.BODY_ACK, 0, null);
            channel.writeAndFlush(packet);

            Protobuf.Message message = Protobuf.Message.parseFrom(body.getContent());
            channel = Session.ONLINE_CHANNEL.get(message.getReceiver());
            if (channel != null) {
                ByteString bs = BuildPacket.buildMessage(0, Type.MESSAGE_SINGLE, message.getText());
                packet = BuildPacket.buildBody(body.getId(), Type.BODY_MESSAGE, body.getSender(), bs);
                channel.writeAndFlush(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ack(Protobuf.Body body) {

    }
}
