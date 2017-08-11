package com.service.im;

import com.service.im.protocol.Protocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UDPMessageServer {

    public static void main(String[] args) {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.option(ChannelOption.SO_BROADCAST, true);
        bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof DatagramPacket) {
                            DatagramPacket packet = (DatagramPacket) msg;
                            ByteBuf buffer = packet.content();
                            byte[] bytes = new byte[buffer.readableBytes()];
                            buffer.readBytes(bytes);
                            System.out.println(new String(bytes));
                            ByteBuf out = Unpooled.buffer();
                            out.writeByte(Protocol.START_TAG);  //起始标记
                            out.writeByte(Protocol.VERSION);    //协议版本
                            out.writeInt(Protocol.HEADER_LENGTH + bytes.length);//包总长度
                            out.writeBytes(Protocol.RETAIN);    //包头保留数组
                            out.writeByte(Protocol.VERIFY_TAG); //包头校验
                            out.writeBytes(bytes);        //内容
                            out.writeByte(Protocol.END_TAG);    //结束标记
                            DatagramPacket dp = new DatagramPacket(out, packet.sender());
                            ctx.writeAndFlush(dp);
//                            System.out.println(packet.sender() + " -> " + packet.content().toString(CharsetUtil.UTF_8));
                        }
                    }
                });
            }
        });
        try {
            bootstrap.bind(6969).sync().channel().closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
