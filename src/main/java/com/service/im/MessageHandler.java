package com.service.im;

import com.service.im.processor.MessageProcessor;
import com.service.im.processor.ProcessorManager;
import com.service.im.protocol.Packet;
import com.service.im.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private ProcessorManager manager;

    public MessageHandler(ProcessorManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet) {
            Packet packet = (Packet) msg;
            packet.channel = ctx.channel();
            MessageProcessor processor = manager.getMessageProcessor(packet.channel);
            if (processor != null) {
                LOGGER.info("分配消息给 [{}]", processor.getName());
                processor.add(packet);
            } else {
                LOGGER.error("无法给 {} 分配消息处理器，消息丢失！", packet.channel.remoteAddress().toString());
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                Channel channel = ctx.channel();
                LOGGER.warn("{} 连接超时! 服务器关闭此连接!", channel.remoteAddress());
                channel.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Session.OFFLINE_CHANNEL.add(channel);
        LOGGER.info("有新连接:{} -> 当前在线人数{}个, 未登录连接数{}个", channel.remoteAddress().toString(), Session.ONLINE_CHANNEL.size(), Session.OFFLINE_CHANNEL.size());
        Attribute<Session> attribute = channel.attr(Session.KEY);
        if (attribute.get() == null) {
            LOGGER.info("设置 {} 的Session", channel.remoteAddress().toString());
            Session session = new Session(System.currentTimeMillis());
            attribute.set(session);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Attribute<Session> attribute = channel.attr(Session.KEY);
        Session session = attribute.get();
        if (session != null) {
            Session.ONLINE_CHANNEL.remove(session.uid);
            if (Session.OFFLINE_CHANNEL.contains(channel)) {
                Session.OFFLINE_CHANNEL.remove(channel);
            }
            attribute.remove();
        }
        LOGGER.info("连接断开:{} -> 当前在线人数{}个, 未登录连接数{}个", channel.remoteAddress().toString(), Session.ONLINE_CHANNEL.size(), Session.OFFLINE_CHANNEL.size());
    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("channelRegistered -> channelActive -> channelInactive -> channelUnregistered");
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("channelRegistered -> channelActive -> channelInactive -> channelUnregistered");
//    }

}