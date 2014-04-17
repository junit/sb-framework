package org.chinasb.common.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Interface used to wrap a java object into javascript object.
 * 
 */
public interface JavascriptWrapper {

    /**
     * wrap an java object into javascript object.
     * 
     * There is a list of warppers in BIRTWrapFactory, the BIRTWrapFactory will call those wrappers
     * one by one to try to wrap the object. If the return object is different with the orginal one,
     * it will terminate the loop.
     * 
     * @param cx context used to execute the wrap.
     * @param scope scope used to execute the wrap.
     * @param javaObject orignal java object
     * @param staticType hint used to wrap this object
     * @return return wrapped object if this interface support the class, return the orignal
     *         directly if it doesn't support such a object.
     */
    public Object wrap(Context cx, Scriptable scope, Object javaObject, Class staticType);
}
