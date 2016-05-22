package org.chinasb.common.basedb;

/**
 * 初始化基础数据接口
 * 
 * @author zhujuan
 */
public interface InitializeBean {
    /**
     * 在属性被设置完后(索引数据前)做一些处理
     */
    public void afterPropertiesSet();
}
