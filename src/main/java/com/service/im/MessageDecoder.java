package com.service.im;

import com.service.im.protocol.Packet;
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
        byte version = in.readByte();   //协议版本
        int length = in.readInt();  //包总长度
        in.readBytes(Protocol.RETAIN);  //保留数组
        byte verify = in.readByte();    //包头校验
        if(verify != '-'){
            LOGGER.error("包头校验失败! 符号{}={} ", verify, (char)verify);
            ctx.close();
            return;
        }
        if (length > canReadLength) {
            //包总长度大于可读包长度则表示包不完整,继续等待下半部分
            in.resetReaderIndex();
            LOGGER.error("可读长度小于包长度[包长度={},可读长度={}] -> {}", length, canReadLength, ctx.channel().remoteAddress());
            return;
        }
        int bodyLength = length - Protocol.HEADER_LENGTH;//计算内容长度
        byte[] body = new byte[bodyLength];
        in.readBytes(body);//内容
        byte end = in.readByte();       //结束标记
        if (end != Protocol.END_TAG) {
            ctx.close();
            LOGGER.error("错误的结束标记 {}", ctx.channel().remoteAddress());
            return;
        }
        Packet packet = new Packet();
        packet.version = version;
        packet.body = body;
        out.add(packet);
    }
}
