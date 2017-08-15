package com.service.im.processor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.service.im.protobuf.Protobuf;
import com.service.im.protobuf.Type;
import com.service.im.protocol.Packet;
import com.service.im.session.Session;
import com.service.im.work.MessageWork;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息处理器
 */
public class MessageProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

    /**
     * 处理器ID
     */
    private int id;

    /**
     * 消息处理器名称
     */
    private String name;

    /**
     * 是否运行
     */
    private boolean run = false;

    /**
     * 未处理消息队列
     */
    private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();

    private MessageWork work = new MessageWork();

    public MessageProcessor(int id) {
        this.id = id;
        this.name = String.format("消息处理器 ID=%d", id);
    }

    @Override
    public void run() {
        run = true;
        LOGGER.info("启动 -> [{}]", name);
        while (run) {
            try {
                Packet packet = queue.take();
                LOGGER.info("[{}] 执行任务", name);
                processor(packet);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("[{}] 消息处理发生异常! {}", name, e);
            }
        }
        run = false;
        LOGGER.info("结束 -> [{}]", name);
    }

    private void processor(Packet packet) throws InvalidProtocolBufferException {
        Protobuf.Body body = Protobuf.Body.parseFrom(packet.body);
        long sender = body.getSender();
        Session session = packet.channel.attr(Session.KEY).get();

        switch (body.getType()) {
            case Type.BODY_ACK:
                work.ack(body);
                break;
            case Type.BODY_LOGIN:
                Protobuf.Login login = Protobuf.Login.parseFrom(body.getContent());
                if (work.login(packet.channel, body.getId(), login)) {
                    Session.OFFLINE_CHANNEL.remove(packet.channel);
                    Channel channel = Session.ONLINE_CHANNEL.get(sender);
                    if(channel != null){
                        channel.close();
                        Session.ONLINE_CHANNEL.remove(sender);
                    }
                    Session.ONLINE_CHANNEL.put(sender, packet.channel);
                    session.uid = sender;
                    LOGGER.info("{} 验证登录连接成功! 当前在线人数{}个, 未登录连接数{}个", packet.channel.remoteAddress(), Session.ONLINE_CHANNEL.size(), Session.OFFLINE_CHANNEL.size());
                }
                break;
            case Type.BODY_MESSAGE:
                work.message(packet.channel, body);
                break;
            case Type.BODY_LOGOUT:

                break;
            case Type.BODY_PUSH:

                break;
            default:
                break;
        }
    }

    public void add(Packet packet) {
        queue.add(packet);
    }

    public int size() {
        return queue.size();
    }

    public boolean isRunning() {
        return run;
    }

    public void stop() {
        run = false;
        queue.add(new Packet());
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
