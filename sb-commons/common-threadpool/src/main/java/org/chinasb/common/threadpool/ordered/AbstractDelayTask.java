package org.chinasb.common.threadpool.ordered;

/**
 * 延时任务抽象
 * 
 * @author zhujuan
 *
 */
public abstract class AbstractDelayTask extends AbstractTask {
	
	protected long delayTime;
	protected long execTime;

	public AbstractDelayTask(TaskQueue taskQueue, int delayTime) {
		super(taskQueue);
		this.delayTime = delayTime;
		this.execTime = System.currentTimeMillis() + delayTime;
	}

	public boolean canExec(long currentTime) {
		if (currentTime >= execTime) {
			taskQueue.enqueue(this);
			return true;
		}
		return false;
	}
	
	@Override
	public void execute() {
		this.execTime = System.currentTimeMillis() + delayTime;
		super.execute();
	}
}
