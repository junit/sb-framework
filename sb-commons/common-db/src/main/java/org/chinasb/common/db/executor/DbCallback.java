package org.chinasb.common.db.executor;

/**
 * 数据库处理回调
 * @author zhujuan
 */
public interface DbCallback {
    /**
     * 完成后处理
     */
    public void doAfter();
}
