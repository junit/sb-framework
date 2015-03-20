package org.chinasb.common.socket.codec;

/**
 * 消息类型
 * @author zhujuan
 *
 */
public enum MessageType {
    /**
     * 字符串
     */
    STRING,
    /**
     * Java Pojo Object
     */
    JAVA,
    /**
     * Amf3 Object
     */
    AMF3,
    /**
     * Json Object
     */
    JSON;
}
