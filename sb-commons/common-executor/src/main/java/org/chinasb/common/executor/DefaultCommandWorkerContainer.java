package org.chinasb.common.executor;

import org.springframework.stereotype.Component;

/**
 * 指令工作器容器
 * @author zhujuan
 */
@Component
public class DefaultCommandWorkerContainer implements CommandWorkerContainer {

    @Override
	public <T> T getWorker(Class<T> workerClazz) {
		if (null == workerClazz) {
			return null;
		}
		T o;
		try {
			o = workerClazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return o;
	}
    
}
