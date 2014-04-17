package org.chinasb.common.rhino;

import java.util.List;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * Represents the scriptable object for Java object which implements the interface List
 * @author zhujuan
 */
public class NativeJavaList extends NativeJavaObject {
    private static final long serialVersionUID = -8793924894604350264L;

    public NativeJavaList() {}

    public NativeJavaList(Scriptable scope, Object javaObject, Class staticType) {
        super(scope, javaObject, staticType);
    }

    public boolean has(int index, Scriptable start) {
        return index >= 0 && index < ((List) javaObject).size();
    }

    public Object get(int index, Scriptable start) {
        return ((List) javaObject).get(index);
    }

    public void put(int index, Scriptable start, Object value) {
        ((List) javaObject).add(index, value);
    }
}
