package org.chinasb.common.executor;

public interface CommandActionQueue {

	/**
	 * 提交即时指令
	 * @param command
	 */
	void submit(Runnable command);

	/**
	 * 提交延时指令
	 * @param command
	 * @param delay
	 */
	void submit(Runnable command, int delay);

}
