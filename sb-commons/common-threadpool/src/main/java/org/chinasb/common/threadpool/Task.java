package org.chinasb.common.threadpool;

/**
 * 任务
 * 
 * @author zhujuan
 *
 */
public interface Task {
	/**
	 * 重置
	 */
	void reset();
	/**
	 * 执行
	 */
    void execute();
}