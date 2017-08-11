package com.service.im.protobuf;

/**
 * 消息类型
 */
public final class Type {

    /**
     * 应答
     */
    byte ACK = -1;

    /**
     * 登录
     */
    byte LOGIN = 0;

    /**
     * 退出
     */
    byte LOGOUT = 1;

    /**
     * 推送消息
     */
    byte PUSH = 4;

    public static final class Chat {
        /**
         * 单向消息(单聊消息)
         */
        byte SINGLE = 2;

        /**
         * 多向消息(群消息)
         */
        byte GROUP = 3;
    }

}
