package org.chinasb.common.executor.watcher;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

public interface WatchEventListener {

	/**
	 * 处理事件
	 * @param watchKey
	 * @param event
	 */
    public void onWatchEvent(WatchKey watchKey, WatchEvent event);

}
