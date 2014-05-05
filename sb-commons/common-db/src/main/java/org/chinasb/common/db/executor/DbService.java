package org.chinasb.common.db.executor;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 数据库服务
 * @author zhujuan
 */
public interface DbService extends ApplicationListener<ContextClosedEvent> {
    static final int DEFAULT_DB_THREADS = Runtime.getRuntime().availableProcessors();
    /**
     * 提交队列
     * @param entities  实体对象
     */
    public <T> void submitUpdate2Queue(T... entities);

    /**
     * 实时更新
     * @param entities  实体对象
     */
    public <T> void updateEntityIntime(T... entities);

    /**
     * 实时更新
     * @param callback  回调参数
     * @param entities  实体对象
     */
    public <T> void updateEntityIntime(DbCallback callback, T... entities);

    /**
     * 判断实体对象是否已在等待持久化的数据集合
     * @param entity
     * @return
     */
    public <T> boolean isInDbQueue(T entity);
}
