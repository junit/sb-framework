package org.chinasb.common.socket.type;
/**
 * 消息返回状态代码
 * @author zhujuan
 */
public interface ResponseCode {
    /**
     * 请求错误
     */
    public static final int RESPONSE_CODE_ERROR = -1;
    /**
     * 请求成功
     */
    public static final int RESPONSE_CODE_SUCCESS = 0;
    /**
     * 没有结果
     */
    public static final int RESPONSE_CODE_NO_RESULTS = 1;
    /**
     * 解析协议错误
     */
    public static final int RESPONSE_CODE_RESOLVE_ERROR = 2;
    /**
     * 消息校验错误
     */
    public static final int RESPONSE_CODE_AUTH_CODE_ERROR = 4;
}
