package com.service.im;

import com.service.im.protocol.Body;
import com.service.im.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int canReadLength = in.readableBytes();
        if (canReadLength < Protocol.HEADER_LENGTH) {
            return;
        }
        in.markReaderIndex();
        byte start = in.readByte();     //起始标记
        if (start != Protocol.START_TAG) {
            LOGGER.error("错误的开始标记 {}", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }
        int length = in.readInt();  //包总长度
        if (length > canReadLength) {
            //包总长度大于可读包长度则表示包不完整,继续等待下半部分
            in.resetReaderIndex();
            LOGGER.warn("可读长度小于包长度[包长度={},可读长度={}] -> {}", length, canReadLength, ctx.channel().remoteAddress());
            return;
        }
        int type = in.readInt(); //包类型
        in.readBytes(Protocol.RETAIN); //读保留头
        int bodyLength = length - Protocol.HEADER_LENGTH;//计算内容长度
        if (bodyLength < 0) {
            LOGGER.error("body长度不正确 {}", bodyLength);
            ctx.close();
            return;
        }
        byte[] body = new byte[bodyLength];
        in.readBytes(body);//内容
        byte end = in.readByte();       //结束标记
        if (end != Protocol.END_TAG) {
            ctx.close();
            LOGGER.error("错误的结束标记 {}", ctx.channel().remoteAddress());
            return;
        }
        out.add(new Body(ctx.channel(), type, body));
    }
}
