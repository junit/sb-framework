package org.chinasb.common.db.executor;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 数据库服务接口
 * 
 * @author zhujuan
 */
public interface DbService extends ApplicationListener<ContextClosedEvent> {
    /**
     * 数据库默认处理线程数量
     */
    static final int DEFAULT_DB_THREADS = Runtime.getRuntime().availableProcessors();

    /**
     * 提交实体到更新队列
     * 
     * @param entities
     */
    @SuppressWarnings("unchecked")
    <T> void submitUpdate2Queue(T... entities);

    /**
     * 实时更新实体
     * 
     * @param entities
     */
    @SuppressWarnings("unchecked")
    <T> void updateEntityIntime(T... entities);

    /**
     * 实时更新实体, 并回调处理更新
     * 
     * @param callback
     * @param entities
     */
    @SuppressWarnings("unchecked")
    <T> void updateEntityIntime(DbCallback callback, T... entities);

    /**
     * 判断实体是否在更新队列中
     * 
     * @param entity
     * @return
     */
    <T> boolean isInDbQueue(T entity);
}
