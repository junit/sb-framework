package org.chinasb.common.disruptor.dispatch;

import org.chinasb.common.disruptor.Event;

/**
 * 任务调度基础抽象
 * @author zhujuan
 */
public abstract class BaseDispatcher implements Dispatcher {
    
    @Override
    public <E extends Event<?>> void dispatch(E event) {
        if (!alive()) {
            throw new IllegalStateException("This Dispatcher has been shutdown");
        }
        Task<E> task = createTask();
        task.setEvent(event);
        task.submit();
    }
    
    protected abstract <E extends Event<?>> Task<E> createTask();

    protected abstract class Task<E extends Event<?>> {
        private volatile E event;

        Task<E> setEvent(E event) {
            this.event = event;
            return this;
        }

        protected void reset() {
            event = null;
        }

        protected abstract void submit();

        protected void execute() {
            ((Runnable) event.getData()).run();
        }
    }
}
