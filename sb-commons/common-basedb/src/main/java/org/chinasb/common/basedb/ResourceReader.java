package org.chinasb.common.basedb;

import java.io.InputStream;
import java.util.Iterator;

/**
 * 资源阅读器
 * @author zhujuan
 */
public interface ResourceReader {
    /**
     * 获取资源格式
     * @return
     */
    public String getFormat();

    /**
     * 读取资源内容
     * @param paramInputStream
     * @param paramClass
     * @return
     */
    public <E> Iterator<E> read(InputStream paramInputStream, Class<E> paramClass);
}
