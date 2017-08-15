package com.service.im.protocol;

import io.netty.channel.Channel;

public class Packet extends Protocol {

    /**
     * 当前消息的管道
     */
    public Channel channel;

    /**
     * 当前消息内容
     */
    public byte[] body;

    public Packet(){

    }

    public Packet(byte[] body){
        this.body = body;
    }

    public Packet(Channel channel, byte[] body){
        this.channel = channel;
        this.body = body;
    }

}
