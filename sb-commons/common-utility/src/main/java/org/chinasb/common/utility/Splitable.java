package org.chinasb.common.utility;

/**
 * 包含可分割字符串属性的对象
 */
public interface Splitable {
	
	/** 
	 * :
	 */
	String DELIMITER_ARGS = ":"; 
	
	/**
	 * ,
	 */
	String BETWEEN_ITEMS = ","; 
	
	/**
	 * \\|
	 */
	String ELEMENT_SPLIT = "\\|";
	
	/**
	 * |
	 */
	String ELEMENT_DELIMITER = "|";
	
	/**
	 * _
	 */
	String ATTRIBUTE_SPLIT = "_";
	
	/**
	 * [
	 */
	String LEFT_PARENTH_SPLIT = "[";
	
	/**
	 * ]
	 */
	String RIGHT_PARENTH_SPLIT = "]";

	/**
	 * _[
	 */
	String LEFT_ELEMENT_SPLIT = "_[";
	
	/**
	 * #
	 */
	String ATTRIBUTE_SPLITE_1 = "#";
	
}
