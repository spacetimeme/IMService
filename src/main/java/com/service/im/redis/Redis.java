package com.service.im.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.nio.ByteBuffer;
import java.util.List;

public class Redis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Redis.class);
    private static Jedis jedis;

    private Redis() {

    }

    public static void doInit() {
        try {
            jedis = new Jedis("127.0.0.1", 6379);
            jedis.connect();
            if (jedis.isConnected()) {
                LOGGER.info("redis 链接成功!!");
            } else {
                LOGGER.error("redis 链接失败!!");
            }
        }catch (Exception e){
            LOGGER.error("redis 链接失败!!", e);
        }

    }

    public static void putMessage(long uid, byte[] body) {
        jedis.rpush(getLongToBytes(uid), body);
    }

    public static List<byte[]> getMessage(long uid) {
        return jedis.lrange(getLongToBytes(uid), 0, -1);
    }

    private static byte[] getLongToBytes(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        buffer.flip();
        return buffer.array();
    }
}
