package org.chinasb.common.rhino;

import java.util.Map;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * Represents the scriptable object for Java object which implements the interface Map
 * @author zhujuan
 */
public class NativeJavaMap extends NativeJavaObject {
    private static final long serialVersionUID = -9072824779336807076L;

    public NativeJavaMap() {}

    public NativeJavaMap(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
    }

    public boolean has(String name, Scriptable start) {
        return ((Map) javaObject).containsKey(name);
    }

    public Object get(String name, Scriptable start) {
        if ("length".equals(name)) {
            return Integer.valueOf(((Map) javaObject).size());
        }
        if (has(name, start)) {
            return ((Map) javaObject).get(name);
        }
        return null;
    }

    public void put(String name, Scriptable start, Object value) {
        ((Map) javaObject).put(name, value);
    }

    public void delete(String name) {
        ((Map) javaObject).remove(name);
    }

    public Object get(int index, Scriptable start) {
        String key = Integer.valueOf(index).toString();
        if (has(key, start)) {
            return ((Map) javaObject).get(key);
        }
        return null;
    }

    public void put(int index, Scriptable start, Object value) {
        ((Map) javaObject).put(Integer.valueOf(index).toString(), value);
    }

    public Object[] getIds() {
        return ((Map) javaObject).keySet().toArray();
    }
}
