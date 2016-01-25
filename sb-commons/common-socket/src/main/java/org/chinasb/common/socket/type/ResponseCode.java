package org.chinasb.common.socket.type;

/**
 * 消息返回状态代码
 * 
 * @author zhujuan
 */
public interface ResponseCode {
    /**
     * 错误
     */
    public static final int RESPONSE_CODE_ERROR = -1;
    /**
     * 成功
     */
    public static final int RESPONSE_CODE_SUCCESS = 0;
    /**
     * 没有结果
     */
    public static final int RESPONSE_CODE_NO_RESULTS = 1;
    /**
     * 协议解析错误
     */
    public static final int RESPONSE_CODE_RESOLVE_ERROR = 2;
    /**
     * 拒绝访问
     */
    public static final int RESPONSE_CODE_FORBIDDEN = 3;
    /**
     * FVN Hash不匹配
     */
    public static final int RESPONSE_CODE_AUTH_CODE_ERROR = 4;
}
