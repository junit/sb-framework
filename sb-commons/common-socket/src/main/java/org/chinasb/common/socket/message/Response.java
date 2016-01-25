package org.chinasb.common.socket.message;

import java.io.Serializable;

import org.chinasb.common.socket.type.ResponseCode;

/**
 * 返回消息
 * 
 * @author zhujuan
 *
 */
public class Response extends Message implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 消息状态
     */
    private int status = ResponseCode.RESPONSE_CODE_SUCCESS;

    /**
     * 获取一个默认的消息对象
     * 
     * @param module 功能模块
     * @param cmd 模块指令
     * @return
     */
    public static Response defaultResponse(int module, int cmd) {
        Response response = new Response();
        response.setSn(DEFAULT_SN);
        response.setModule(module);
        response.setCmd(cmd);
        return response;
    }

    /**
     * 获取一个默认的消息对象
     * 
     * @param module 功能模块
     * @param cmd 模块指令
     * @param value 消息内容
     * @return
     */
    public static Response defaultResponse(int module, int cmd, Object value) {
        Response response = defaultResponse(module, cmd);
        response.setValue(value);
        return response;
    }

    /**
     * 获取一个消息返回对象
     * 
     * @param sn 消息序列号
     * @param module 功能模块
     * @param cmd 模块指令
     * @return
     */
    public static Response valueOf(int sn, int module, int cmd) {
        Response response = new Response();
        response.setSn(sn);
        response.setModule(module);
        response.setCmd(cmd);
        return response;
    }

    /**
     * 获取一个消息返回对象
     * 
     * @param sn 消息序列号
     * @param module 功能模块
     * @param cmd 模块指令
     * @param value 消息内容
     * @return
     */
    public static Response valueOf(int sn, int module, int cmd, Object value) {
        Response response = valueOf(sn, module, cmd);
        response.setValue(value);
        return response;
    }

    /**
     * 获取一个消息返回对象
     * 
     * @param sn 消息序列号
     * @param module 功能模块
     * @param cmd 模块指令
     * @param messageType 消息类型
     * @param status 消息状态
     * @return
     */
    public static Response valueOf(int sn, int module, int cmd, int messageType, int status) {
        Response response = valueOf(sn, module, cmd);
        response.setStatus(status);
        response.setMessageType(messageType);
        return response;
    }

    /**
     * 获取一个消息返回对象
     * 
     * @param sn 消息序列号
     * @param module 功能模块
     * @param cmd 模块指令
     * @param messageType 消息类型
     * @param status 消息状态
     * @param value 消息内容
     * @return
     */
    public static Response valueOf(int sn, int module, int cmd, int messageType, int status,
            Object value) {
        Response response = valueOf(sn, module, cmd, messageType, status);
        response.setValue(value);
        return response;
    }

    /**
     * 获取消息状态
     * 
     * @return
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * 设置消息状态
     * 
     * @param status
     * @return
     */
    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "Response" + super.toString();
    }
}
