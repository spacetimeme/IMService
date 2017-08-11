package com.service.im.processor;

import com.service.im.session.Session;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * 连接未登录处理器，解决不登录的连接资源占用
 */
public class ScheduledProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledProcessor.class);
    private int timeOut;

    public ScheduledProcessor(int timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public void run() {
        Iterator<Channel> iterator = Session.OFFLINE_CHANNEL.iterator();
        long currentTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            Attribute<Session> attribute = channel.attr(Session.KEY);
            Session session = attribute.get();
            if (session == null) {
                LOGGER.error("{} 没有绑定Session", channel.remoteAddress().toString());
                channel.close();
                iterator.remove();
                continue;
            }
            if ((currentTime - session.connectTime) >= timeOut) {
                LOGGER.warn("{} 长时间没有登录, 关闭连接! {}", channel.remoteAddress(), session.uid);
                channel.close();
                iterator.remove();
            }
        }
    }
}
