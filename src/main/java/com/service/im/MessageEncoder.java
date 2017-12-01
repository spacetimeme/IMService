package com.service.im;

import com.service.im.protocol.Body;
import com.service.im.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Body> {

    //{起始标记   -byte     - 1}
    //{包总长度   -int      - 4}
    //{包的类型   -int      - 4}
    //{发送者ID   -int      - 4}
    //{接受者ID   -int      - 4}
    //{消息ID    -byte[32] - 32}
    //{包体内容   -byte[n]  - n}
    //{结束标记   -byte     - 1}
    @Override
    protected void encode(ChannelHandlerContext ctx, Body body, ByteBuf out) throws Exception {
        out.writeByte(Protocol.START_TAG);  //起始标记
        out.writeInt(Protocol.HEADER_LENGTH + body.getBody().length);//包总长度
        out.writeInt(body.getType());
        out.writeInt(body.getSender());
        out.writeInt(body.getRecipient());
        out.writeBytes(body.getId().getBytes());
        out.writeBytes(body.getBody());        //内容
        out.writeByte(Protocol.END_TAG);    //结束标记
    }
}
