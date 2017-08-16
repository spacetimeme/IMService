package com.service.im.session;

import io.netty.util.AttributeKey;

public class Session {

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
    public int uid = -1;

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
