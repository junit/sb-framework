package org.chinasb.common.socket.push;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.aeonbits.owner.ConfigCache;
import org.chinasb.common.socket.SessionManager;
import org.chinasb.common.socket.config.ServerConfig;
import org.chinasb.common.socket.message.Response;
import org.chinasb.common.threadpool.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 异步消息推送
 * 
 * @author zhujuan
 *
 */
@Component
public final class Pusher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pusher.class);
    @Autowired
    private SessionManager sessionManager;
    private final int COMMON_QUEUE_SIZE = ConfigCache.getOrCreate(ServerConfig.class)
            .commonQueueSize();
    @SuppressWarnings("unchecked")
    private final BlockingQueue<PushContext>[] PUSHER_QUEUE_ARRAY =
            new LinkedBlockingQueue[COMMON_QUEUE_SIZE];

    /**
     * 推送消息
     * 
     * @param playerId 玩家ID
     * @param response 消息内容
     */
    public void pushMessage(long playerId, Response response) {
        if (response != null) {
            putMessage2Queue(PushContext.push2Player(playerId, response));
        }
    }

    /**
     * 推送消息
     * 
     * @param playerIdList 玩家ID列表
     * @param response 消息内容
     */
    public void pushMessage(Collection<Long> playerIdList, Response response) {
        if ((playerIdList != null) && (!playerIdList.isEmpty()) && (response != null)) {
            putMessage2Queue(PushContext.push2Players(playerIdList, response));
        }
    }

    /**
     * 推送消息到所有在线玩家
     * 
     * @param response
     */
    public void pushMessage2All(Response response) {
        if (response != null) {
            putMessage2Queue(PushContext.push2AllOnline(response));
        }
    }

    /**
     * 添加消息到推送队列
     * 
     * @param pushContext
     */
    private void putMessage2Queue(PushContext pushContext) {
        if (pushContext == null) {
            return;
        }
        Response response = pushContext.getResponse();
        if (response == null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int index = (int) (currentTimeMillis % COMMON_QUEUE_SIZE);
        try {
            PUSHER_QUEUE_ARRAY[index].add(pushContext);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("推送信息放入队列成功 [module: [%d}, cmd: [%d] ]", new Object[] {
                        Integer.valueOf(response.getModule()), Integer.valueOf(response.getCmd())}));
            }
        } catch (Exception ex) {
            LOGGER.error("putMessage2Queue error: {}", ex);
            LOGGER.error("{}", ex);
        }
    }

    @PostConstruct
    void initialize() {
        String threadName = "异步推送线程";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialize System Pusher Thread... 总数量:[{}]",
                    Integer.valueOf(COMMON_QUEUE_SIZE));
        }
        for (int index = 0; index < COMMON_QUEUE_SIZE; index++) {
            BlockingQueue<PushContext> queue = PUSHER_QUEUE_ARRAY[index];
            if (queue == null) {
                PUSHER_QUEUE_ARRAY[index] = new LinkedBlockingQueue<PushContext>(Integer.MAX_VALUE);
                queue = PUSHER_QUEUE_ARRAY[index];
            }
            NamedThreadFactory factory = new NamedThreadFactory(threadName + index, true);
            Thread thread = factory.newThread(getCustomerThread(queue));
            thread.start();
        }
    }

    public final Runnable getCustomerThread(final BlockingQueue<PushContext> commonQueue) {
        return new Runnable() {
            public void run() {
                try {
                    for (;;) {
                        PushContext pushContext = (PushContext) commonQueue.take();
                        if (pushContext != null) {
                            Response response = pushContext.getResponse();
                            if (response == null) {
                                LOGGER.error(String.format("异步推送处理, 收到Response:[%s] 不合法: ",
                                        new Object[] {response}));
                            } else {
                                int cmd = response.getCmd();
                                int module = response.getModule();
                                PushType type = pushContext.getType();
                                Collection<Long> playerIdList = pushContext.getPlayerIdList();
                                if (type == PushType.ALL) {
                                    playerIdList = sessionManager.getOnlinePlayerIdList();
                                }
                                sessionManager.write(playerIdList, response);
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug(String.format(
                                            "从队列推送信息成功 [module: %d, cmd: %d ]",
                                            new Object[] {Integer.valueOf(module),
                                                    Integer.valueOf(cmd)}));
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error("Error: {}", ex);
                    LOGGER.error("{}", ex);
                }
            }
        };
    }
}
