package org.chinasb.common.threadpool;

import java.util.concurrent.Executor;

import org.chinasb.common.URL;

/**
 * 
 * @author zhujuan
 *
 */
public interface ExecutorFactory {
    Executor getExecutor();

    Executor getExecutor(URL url);
}