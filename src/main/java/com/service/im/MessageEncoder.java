package com.service.im;

import com.service.im.protocol.Body;
import com.service.im.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Body> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Body body, ByteBuf out) throws Exception {
        out.writeByte(Protocol.START_TAG);  //起始标记
        out.writeInt(Protocol.HEADER_LENGTH + body.getLength());//包总长度
        out.writeBytes(Protocol.RETAIN);    //包头保留数组
        out.writeBytes(body.getArray());        //内容
        out.writeByte(Protocol.END_TAG);    //结束标记
    }
}
