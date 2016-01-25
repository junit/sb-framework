package org.chinasb.common.socket.message;

/**
 * 请求消息
 * 
 * @author zhujuan
 *
 */
public class Request extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * 获取一个消息对象
     * 
     * @param sn 消息序列号
     * @param module 功能模块
     * @param cmd 模块指令
     * @return
     */
    public static Request valueOf(int sn, int module, int cmd) {
        Request request = new Request();
        request.setSn(sn);
        request.setModule(module);
        request.setCmd(cmd);
        return request;
    }

    /**
     * 获取一个消息对象
     * 
     * @param sn 消息序列号
     * @param module 功能模块
     * @param cmd 模块指令
     * @param messageType 消息类型
     * @return
     */
    public static Request valueOf(int sn, int module, int cmd, int messageType) {
        Request request = valueOf(sn, module, cmd);
        request.setMessageType(messageType);
        return request;
    }

    /**
     * 获取一个消息对象
     * 
     * @param sn 消息序列号
     * @param module 功能模块
     * @param cmd 模块指令
     * @param messageType 消息类型
     * @param value 消息内容
     * @return
     */
    public static Request valueOf(int sn, int module, int cmd, int messageType, Object value) {
        Request request = valueOf(sn, module, cmd, messageType);
        request.setValue(value);
        return request;
    }

    @Override
    public String toString() {
        return "Request" + super.toString();
    }
}
