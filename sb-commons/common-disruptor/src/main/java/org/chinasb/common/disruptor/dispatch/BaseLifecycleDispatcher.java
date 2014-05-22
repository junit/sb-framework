package org.chinasb.common.disruptor.dispatch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 任务调度生命周期抽象
 * @author zhujuan
 */
public abstract class BaseLifecycleDispatcher extends BaseDispatcher {

    private final AtomicBoolean alive = new AtomicBoolean(true);

    @Override
    public boolean alive() {
        return alive.get();
    }

    @Override
    public boolean awaitAndShutdown() {
        return awaitAndShutdown(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        alive.compareAndSet(true, false);
    }

    @Override
    public void halt() {
        alive.compareAndSet(true, false);
    }
}
