package com.service.im.processor;

import com.service.im.session.OnlineChannel;

/**
 * 连接未登录处理器，解决不登录的连接资源占用
 */
public class ScheduledProcessor implements Runnable {

    private int timeOut;

    public ScheduledProcessor(int timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public void run() {
        OnlineChannel.timeOut(this.timeOut);
    }
}
