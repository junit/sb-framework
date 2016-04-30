package org.chinasb.common.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * 
 * <pre>
 * json工具类
 * 
 * @author zhujuan
 * </pre>
 */
public class JSONUtils {

	public static final String EMPTY_JSON = "{}";
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final ObjectWriter WRITER = OBJECT_MAPPER.writer();
	private static final ObjectWriter PRETTY_WRITER =
			OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
	
	static {
		OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static String toJsonPrettyString(Object value) {
		if (value == null) {
			return EMPTY_JSON;
		}
		try {
			return PRETTY_WRITER.writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static String toJsonString(Object value) {
		if (value == null) {
			return EMPTY_JSON;
		}
		try {
			return WRITER.writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static <T> T fromJsonString(String json, Class<T> clazz) {
		if (json == null || clazz == null) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse Json String.", e);
		}
	}
	
	public static <T> List<T> fromJsonString2List(String json, Class<T> clazz) {
		if (json == null || clazz == null) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(json,
					TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse Json String.", e);
		}
	}
	
	public static <K, V> Map<K, V> fromJsonString2Map(String json, Class<K> keyClazz, Class<V> valueClazz) {
		if (json == null || keyClazz == null || valueClazz == null) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(json, TypeFactory.defaultInstance().constructMapType(Map.class, keyClazz, valueClazz));
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse Json String.", e);
		}
	}
	
	public static JsonNode jsonNodeOf(String json) {
		return fromJsonString(json, JsonNode.class);
	}

	public static JsonGenerator jsonGeneratorOf(Writer writer) throws IOException {
		return new JsonFactory().createGenerator(writer);
	}

	public static <T> T loadFrom(File file, Class<T> clazz) throws IOException {
		try {
			return OBJECT_MAPPER.readValue(file, clazz);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void load(InputStream input, Object obj)
			throws IOException, JsonProcessingException {
		OBJECT_MAPPER.readerForUpdating(obj).readValue(input);
	}

	public static <T> T loadFrom(InputStream input, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		return OBJECT_MAPPER.readValue(input, clazz);
	}

	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}

	public static ObjectWriter getWriter() {
		return WRITER;
	}

	public static ObjectWriter getPrettyWriter() {
		return PRETTY_WRITER;
	}
}
