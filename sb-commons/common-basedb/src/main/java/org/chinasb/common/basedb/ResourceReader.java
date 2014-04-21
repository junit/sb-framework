package org.chinasb.common.basedb;

import java.io.InputStream;
import java.util.Iterator;

/**
 * 资源读取
 * @author zhujuan
 */
public interface ResourceReader {
    /**
     * 获得格式
     * @return
     */
    public String getFormat();

    /**
     * 读取资源
     * @param paramInputStream
     * @param paramClass
     * @return
     */
    public <E> Iterator<E> read(InputStream paramInputStream, Class<E> paramClass);
}
