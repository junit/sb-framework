package org.chinasb.common.disruptor.handler;

import org.chinasb.common.disruptor.event.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * 消息处理器
 * @author zhujuan
 */
public class MessageEventHandler implements EventHandler<MessageEvent>, WorkHandler<MessageEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEventHandler.class);

    @Override
    public void onEvent(final MessageEvent event, final long sequence, final boolean endOfBatch)
            throws Exception {
        try {
            event.task.run();
        } catch (Exception e) {
            LOGGER.error("EXECUTE TASK ERROR:", e);
        }
    }

    @Override
    public void onEvent(MessageEvent event) throws Exception {
        try {
            event.task.run();
        } catch (Exception e) {
            LOGGER.error("EXECUTE TASK ERROR:", e);
        }
    }
}
