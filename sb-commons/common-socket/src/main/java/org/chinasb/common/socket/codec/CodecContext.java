package org.chinasb.common.socket.codec;

import java.io.Serializable;

/**
 * 编解码上下文信息
 * 
 * @author zhujuan
 *
 */
public class CodecContext implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 数据包所需字节数量
     */
    private int bytesNeeded = 0;
    /**
     * 解码状态
     */
    private DecoderState state = DecoderState.WAITING_DATA;

    /**
     * 获取数据包所需字节数量
     * 
     * @return
     */
    public int getBytesNeeded() {
        return this.bytesNeeded;
    }

    /**
     * 设置数据包所需字节数量
     * 
     * @param bytesNeeded
     */
    public void setBytesNeeded(int bytesNeeded) {
        this.bytesNeeded = bytesNeeded;
    }

    /**
     * 获取解码状态
     * 
     * @return
     */
    public DecoderState getState() {
        return this.state;
    }

    /**
     * 设置解码状态
     * 
     * @param state
     */
    public void setState(DecoderState state) {
        this.state = state;
    }

    /**
     * 比较解码状态是否相同
     * 
     * @param state
     * @return
     */
    public boolean isSameState(DecoderState state) {
        return (this.state != null) && (state != null) && (this.state == state);
    }

    /**
     * 返回一个新的编解码上下文对象
     * 
     * @param byteNeeded 数据包所需字节数量
     * @param state 解码状态
     * @return
     */
    public static CodecContext valueOf(int byteNeeded, DecoderState state) {
        CodecContext codecContext = new CodecContext();
        codecContext.setBytesNeeded(byteNeeded);
        codecContext.setState(state);
        return codecContext;
    }
}
