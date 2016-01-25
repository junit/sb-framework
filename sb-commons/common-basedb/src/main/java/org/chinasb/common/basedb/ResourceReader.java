package org.chinasb.common.basedb;

import java.io.InputStream;
import java.util.Iterator;

/**
 * 资源阅读器
 * 
 * @author zhujuan
 */
public interface ResourceReader {
    /**
     * 获取资源格式
     * 
     * @return
     */
    public String getFormat();

    /**
     * 从输入流读取资源实例
     * 
     * @param <E>
     * @param input 输入流
     * @param clz 资源实例类型
     * @return
     */
    public <E> Iterator<E> read(InputStream paramInputStream, Class<E> paramClass);
}
