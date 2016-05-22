package org.chinasb.common.basedb;

/**
 * 资源监听器
 * 
 * @author zhujuan
 */
public interface ResourceListener {
    /**
     * 当基础数据重载后调用
     */
    public void onBasedbReload();
}
