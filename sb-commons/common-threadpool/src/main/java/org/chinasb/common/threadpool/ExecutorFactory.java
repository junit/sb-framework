package org.chinasb.common.threadpool;

import java.util.concurrent.Executor;

import org.chinasb.common.utility.URL;

/**
 * 线程池工厂管理
 * 
 * @author zhujuan
 *
 */
public interface ExecutorFactory {
    /**
     * 获取一个默认的线程池
     * 
     * @return
     */
    Executor getExecutor();

    /**
     * 获取一个指定配置的线程池
     * 
     * @param url 配置信息
     * @return
     */
    Executor getExecutor(URL url);
}
