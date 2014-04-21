package org.chinasb.common.rhino;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class CoreJavaScriptWrapper implements JavascriptWrapper {

    @Override
    public Object wrap(Context cx, Scriptable scope, Object javaObject, Class staticType) {
        if (javaObject instanceof Map) {
            return new NativeJavaMap(scope, javaObject, staticType);
        }
        if (javaObject instanceof List) {
            return new NativeJavaList(scope, javaObject, staticType);
        }
        return javaObject;
    }

}
