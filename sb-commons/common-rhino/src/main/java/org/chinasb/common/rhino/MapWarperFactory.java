package org.chinasb.common.rhino;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 * 公式参数包装器工厂 - Map 包装器
 */
@SuppressWarnings("rawtypes")
public class MapWarperFactory extends WrapFactory {

	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		if (obj instanceof Map) {
			return NativeJavaMap.wrap((Map) obj);
		}
		return super.wrap(cx, scope, obj, staticType);
	}

	@Override
	public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
		if (obj instanceof Map) {
			return NativeJavaMap.wrap((Map) obj);
		}
		return super.wrapNewObject(cx, scope, obj);
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		if (obj instanceof Map) {
			return NativeJavaMap.wrap((Map) obj);
		}
		return super.wrapAsJavaObject(cx, scope, obj, staticType);
	}

	@Override
	public Scriptable wrapJavaClass(Context cx, Scriptable scope,
			Class javaClass) {
		return super.wrapJavaClass(cx, scope, javaClass);
	}

}
