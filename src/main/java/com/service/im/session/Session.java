package com.service.im.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    /**
     * 存放连接但未登录的Channel
     */
    public static final List<Channel> OFFLINE_CHANNEL = new Vector<>();

    /**
     * 存放已登录的Channel
     * key是登录用户的编号UID
     */
    public static final Map<Long, Channel> ONLINE_CHANNEL = new ConcurrentHashMap<>();

    /**
     * 每个Channel的数据key
     */
    public static final AttributeKey<Session> KEY = AttributeKey.valueOf("session");

    /**
     * 消息处理器ID
     */
    public int processorId = -1;

    /**
     * 登录用户编号，连接未登录为-1
     */
    public long uid = -1;

    /**
     * 连接服务器的时间。用于那些连接但未登录验证的Channel
     */
    public long connectTime;

    public Session() {

    }

    public Session(long connectTime) {
        this.connectTime = connectTime;
    }

}
