package com.service.im.processor;

import com.service.im.session.Session;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 处理器管理
 */
public class ProcessorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorManager.class);

    /**
     * 消息处理器线程池数量
     */
    private static final int SIZE = 6;

    /**
     * 离线链接超时
     */
    private static final int OFFLINE_TIME_OUT = 1000 * 60;

    /**
     * 消息处理器集合
     */
    private Map<Integer, MessageProcessor> processors = new HashMap<>();

    /**
     * 定时处理不登录连接处理器
     */
    private ScheduledProcessor scheduled;

    /**
     * 消息处理器线程池
     */
    private ExecutorService processorPool;

    /**
     * 定时任务线程池
     */
    private ScheduledExecutorService scheduledPool;

    public ProcessorManager() {
        processorPool = Executors.newFixedThreadPool(SIZE);
        scheduled = new ScheduledProcessor(OFFLINE_TIME_OUT);
        scheduledPool = Executors.newScheduledThreadPool(1);
    }

    /**
     * 获取相对空闲的消息处理器,就是查找处理任务少的
     * 可能需要同步
     * @return 任务处理器
     */
    public MessageProcessor getMessageProcessor() {
        int min = Integer.MAX_VALUE;
        Collection<MessageProcessor> collection = processors.values();
        MessageProcessor processor = null;
        for (MessageProcessor mp : collection) {
            if (mp.isRunning()) {
                int count = mp.size();
                if (count == 0) {
                    processor = mp;
                    break;
                }
                if (count < min) {
                    min = count;
                    processor = mp;
                }
            } else {
                LOGGER.error("[{}] 没有运行, 可能异常终止了! 重新启动处理器", mp.getName());
                processorPool.execute(mp);
            }
        }
        if (processor != null) {
            LOGGER.debug("获取到 [{}] !", processor.getName());
        } else {
            LOGGER.error("未找到合适的处理器!");
            throw new NullPointerException("没有找到合适的处理器");
        }
        return processor;
    }

    /**
     * 开启所有处理器
     */
    public void start() {
        for (int i = 0; i < SIZE; i++) {
            MessageProcessor processor = new MessageProcessor(i);
            processors.put(i, processor);
            processorPool.execute(processor);
        }
        scheduledPool.scheduleAtFixedRate(scheduled, 1000, 6000, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取已经绑定的处理器
     *
     * @return
     */
    public MessageProcessor getMessageProcessor(Channel channel) {
        Session session = channel.attr(Session.KEY).get();
        MessageProcessor processor;
        if (session.processorId < 0) {
            processor = getMessageProcessor();
            session.processorId = processor.getId();
        } else {
            processor = processors.get(session.processorId);
        }
        return processor;
    }

    /**
     * 停止所有处理器
     */
    public void stop() {
        Collection<MessageProcessor> collection = processors.values();
        for (MessageProcessor processor : collection) {
            processor.stop();
        }
        collection.clear();
        processors = null;
        processorPool.shutdown();
        scheduledPool.shutdown();
    }
}
