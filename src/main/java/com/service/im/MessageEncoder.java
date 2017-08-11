package com.service.im;

import com.service.im.protocol.Packet;
import com.service.im.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        out.writeByte(Protocol.START_TAG);  //起始标记
        out.writeByte(Protocol.VERSION);    //协议版本
        out.writeInt(Protocol.HEADER_LENGTH + packet.body.length);//包总长度
        out.writeBytes(Protocol.RETAIN);    //包头保留数组
        out.writeByte(Protocol.VERIFY_TAG); //包头校验
        out.writeBytes(packet.body);        //内容
        out.writeByte(Protocol.END_TAG);    //结束标记
    }
}
