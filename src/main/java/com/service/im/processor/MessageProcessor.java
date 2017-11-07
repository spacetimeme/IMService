package com.service.im.processor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.service.im.protobuf.BodyType;
import com.service.im.protobuf.Protobuf;
import com.service.im.protocol.Packet;
import com.service.im.session.ChannelGroup;
import com.service.im.session.Session;
import com.service.im.work.MessageWork;
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
        LOGGER.debug("启动 -> [{}]", name);
        while (run) {
            try {
                Packet packet = queue.take();
                if(run){
                    LOGGER.debug("[{}] 执行任务", name);
                    processor(packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("[{}] 消息处理发生异常! {}", name, e);
            }
        }
        run = false;
        LOGGER.debug("结束 -> [{}]", name);
    }

    private void processor(Packet packet) throws InvalidProtocolBufferException {
        Protobuf.Body body = Protobuf.Body.parseFrom(packet.body);
        int sender = body.getSender();
        if (sender <= 0) {
            LOGGER.warn("发送方ID={}, 此包被丢弃！", sender);
            return;
        }
        switch (BodyType.getType(body.getType())) {
            case ACK:
                work.ack(body);
                break;
            case LOGIN:
                Protobuf.Login login = Protobuf.Login.parseFrom(body.getContent());
                if (work.login(packet.channel, body.getId(), login)) {
                    ChannelGroup.online(sender, packet.channel);
                    Session session = packet.channel.attr(Session.KEY).get();
                    session.uid = sender;
                    LOGGER.info("uid={} -> {} 验证登录连接成功! 当前在线人数{}个, 未登录连接数{}个", sender, packet.channel.remoteAddress(), ChannelGroup.getOnlineSize(), ChannelGroup.getConnectedSize());
                }
                break;
            case MESSAGE:
                work.message(packet.channel, body);
                break;
            case LOGOUT:
                work.logout(packet.channel, body);
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
