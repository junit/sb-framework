package org.chinasb.common.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * 消息事件
 * @author zhujuan
 */
public class MessageEvent {
    
    /** 事件任务 */
    public Runnable task;
    
    /** 事件工厂 */
    public static final EventFactory<MessageEvent> EVENT_FACTORY =
            new EventFactory<MessageEvent>() {
                @Override
                public MessageEvent newInstance() {
                    return new MessageEvent();
                }
            };
}
