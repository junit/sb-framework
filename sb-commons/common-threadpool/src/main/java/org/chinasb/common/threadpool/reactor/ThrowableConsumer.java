package org.chinasb.common.threadpool.reactor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.fn.Consumer;

/**
 * 
 * @author zhujuan
 *
 */
public class ThrowableConsumer implements Consumer<Throwable> {

    private final Logger logger;

    public ThrowableConsumer(Class<?> aClass) {
        this.logger = LoggerFactory.getLogger(aClass);
    }

    @Override
    public void accept(Throwable throwable) {
        logger.error("Can not process task {}", throwable);
    }
}
