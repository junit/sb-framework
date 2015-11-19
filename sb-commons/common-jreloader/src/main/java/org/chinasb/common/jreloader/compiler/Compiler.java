package org.chinasb.common.jreloader.compiler;

/**
 * compiler.
 * 
 * @author zhujuan
 */
public interface Compiler {
    
	/**
	 * Compile java source code.
	 * 
	 * @param code Java source code
	 * @return Compiled class
	 */
	Class<?> compile(String code);

}
