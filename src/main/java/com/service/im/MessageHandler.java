package com.service.im;

import com.service.im.processor.MessageProcessor;
import com.service.im.processor.ProcessorManager;
import com.service.im.protocol.Body;
import com.service.im.session.ChannelGroup;
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
        if (msg instanceof Body) {
            Body body = (Body) msg;
            MessageProcessor processor = manager.getMessageProcessor(body.getSession());
            if (processor != null) {
                LOGGER.debug("分配消息给 [{}]", processor.getName());
                processor.add(body);
            } else {
                LOGGER.error("无法给 {} 分配消息处理器，消息丢失！", body.getChannel().remoteAddress().toString());
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
        Attribute<Session> attribute = channel.attr(Session.KEY);
        if (attribute.get() == null) {
            LOGGER.info("创建Session -> {}", channel.remoteAddress().toString());
            attribute.set(new Session(System.currentTimeMillis()));
        }
        ChannelGroup.connected(channel);
        LOGGER.info("有新连接:{} -> 当前在线人数{}个, 未登录连接数{}个", channel.remoteAddress().toString(), ChannelGroup.getOnlineSize(), ChannelGroup.getConnectedSize());

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Attribute<Session> attribute = channel.attr(Session.KEY);
        Session session = attribute.get();
        if (session != null) {
            ChannelGroup.disconnect(session.uid, channel);
            attribute.remove();
            attribute.set(null);
        } else {
            ChannelGroup.disconnect(channel);
        }
        LOGGER.info("连接断开:{} -> 当前在线人数{}个, 未登录连接数{}个", channel.remoteAddress(), ChannelGroup.getOnlineSize(), ChannelGroup.getConnectedSize());
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
