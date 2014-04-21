package org.chinasb.common.basedb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * JSON
 * @author zhujuan
 */
@Component("jsonResourceReader")
public class JsonResourceReader implements ResourceReader {
    private static final Logger log = LoggerFactory.getLogger(JsonResourceReader.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    @Override
    public String getFormat() {
        return "json";
    }
    
    @Override
    public <E> Iterator<E> read(InputStream input, Class<E> clazz) {
        try {
            JavaType type = typeFactory.constructCollectionType(ArrayList.class, clazz);
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<E> list = objectMapper.readValue(input, type);
            return list.iterator();
        } catch (Exception e) {
            log.error("JsonReader读取基础数据:[{}] 文件异常!", clazz, e);
            throw new RuntimeException(e);
        }
    }
}
