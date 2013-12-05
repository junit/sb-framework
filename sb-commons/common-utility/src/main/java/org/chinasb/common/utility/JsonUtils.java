package org.chinasb.common.utility;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class JsonUtils {
    
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String map2String(Map<?, ?> map) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, map);
        } catch (Exception e) {
            log.error("将 map 转换为 json 字符串时发生异常", e);
            return null;
        }
        return writer.toString();
    }

    public static Map<?, ?> string2Map(String string) {
        StringReader reader = new StringReader(string);
        try {
            return mapper.readValue(reader, HashMap.class);
        } catch (IOException e) {
            log.error("将 json 字符串转换为 HashMap 时发生异常", e);
        }
        return null;
    }

    public static String list2String(Collection<Map<String, Object>> map) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, map);
        } catch (Exception e) {
            log.error("将 collection 转换为 json 字符串时发生异常", e);
            return null;
        }
        return writer.toString();
    }
}
