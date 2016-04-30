package org.chinasb.common.utility;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义命合的守护ThreadFactory
 * 
 * @author zhujuan
 *
 */
public class NamedDaemonThreadFactory implements ThreadFactory {

	private static final AtomicInteger COUNTER = new AtomicInteger(0);

	private final String prefix;

	public NamedDaemonThreadFactory(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread t = new Thread(runnable);
		t.setName(prefix + "-" + COUNTER.incrementAndGet());
		t.setDaemon(true);
		return t;
	}

}