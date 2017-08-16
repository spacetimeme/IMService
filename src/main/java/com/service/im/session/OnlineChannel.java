package com.service.im.session;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineChannel extends ConcurrentHashMap<Integer, Channel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineChannel.class);

    /**
     * 存放已登录的Channel
     * key是登录用户的编号UID
     */
    private static final Map<Integer, Channel> ONLINE_CHANNEL = new ConcurrentHashMap<>();

    /**
     * 存放连接但未登录的Channel
     */
    public static final Vector<Channel> CONNECTED_CHANNEL = new Vector<>();

    public static void online(Integer id, Channel channel) {
        CONNECTED_CHANNEL.remove(channel);
        ONLINE_CHANNEL.put(id, channel);
    }

    public static Channel getOnlineChannel(Integer id) {
        return ONLINE_CHANNEL.get(id);
    }

    public static void connect(Channel channel) {
        CONNECTED_CHANNEL.add(channel);
    }

    public static void disconnect(Channel channel) {
        disconnect(-1, channel);
    }

    public static void disconnect(Integer id, Channel channel) {
        if (id > 0) {
            ONLINE_CHANNEL.remove(id);
        }
        if (channel != null) {
            CONNECTED_CHANNEL.remove(channel);
        }
    }

    public static int getOnlineSize() {
        return ONLINE_CHANNEL.size();
    }

    public static int getConnectSize() {
        return CONNECTED_CHANNEL.size();
    }

    public static void timeOut(long timeOut) {
        Iterator<Channel> iterator = CONNECTED_CHANNEL.iterator();
        long currentTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            Attribute<Session> attribute = channel.attr(Session.KEY);
            Session session = attribute.get();
            if (session == null) {
                LOGGER.warn("{} 没有绑定Session", channel.remoteAddress().toString());
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
