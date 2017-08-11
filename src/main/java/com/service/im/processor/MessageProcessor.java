package com.service.im.processor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.service.im.protobuf.Protobuf;
import com.service.im.protocol.Packet;
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
//        Protobuf.Message message = Protobuf.Message.parseFrom(packet.body);
//        System.out.println(new String(message.getContent().toByteArray()));
////        System.out.println(Protobuf.Text.parseFrom(message.getContent().toByteArray()).getText());
//        packet.body = message.getContent().toByteArray();
//        packet.channel.writeAndFlush(packet);
//        Protobuf.Conversation conversation = Protobuf.Conversation.parseFrom(message.getContent());
//        switch (message.getType()) {
//            case Type.LOGIN://连接发送登录验证成功后，移除未登录列表，并添加到登录列表，并且不要忘记设置Session的uid
//                String token = new String(message.getContent().toByteArray());
//                long uid = message.getSender();
//                Session session = packet.channel.attr(Session.KEY).get();
//                if (session.uid > 0 && (session.uid != uid)) {
//                    Session.ONLINE_CHANNEL.remove(session.uid);
//                    LOGGER.info("{} 重置用户连接! 当前在线人数{}个, 未登录连接数{}个", packet.channel.remoteAddress(), Session.ONLINE_CHANNEL.size(), Session.OFFLINE_CHANNEL.size());
//                } else {
//                    Session.OFFLINE_CHANNEL.remove(packet.channel);
//                    LOGGER.info("{} 验证登录连接成功! 当前在线人数{}个, 未登录连接数{}个", packet.channel.remoteAddress(), Session.ONLINE_CHANNEL.size(), Session.OFFLINE_CHANNEL.size());
//                }
//                Session.ONLINE_CHANNEL.put(uid, packet.channel);
//                session.uid = uid;
//                break;
//        }
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
