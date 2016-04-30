package org.chinasb.common.basedb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.chinasb.common.utility.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * JSON 资源读取器
 * 
 * @author zhujuan
 */
@Component("jsonResourceReader")
public class JsonResourceReader implements ResourceReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonResourceReader.class);

    private static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    @Override
    public String getFormat() {
        return "json";
    }

    @Override
    public <E> Iterator<E> read(InputStream input, Class<E> clazz) {
        try {
            JavaType type = typeFactory.constructCollectionType(ArrayList.class, clazz);
            List<E> list = JSONUtils.getObjectMapper().readValue(input, type);
            return list.iterator();
        } catch (Exception e) {
            LOGGER.error("JsonReader读取基础数据:[{}] 文件异常!", clazz, e);
            throw new RuntimeException(e);
        }
    }
}
