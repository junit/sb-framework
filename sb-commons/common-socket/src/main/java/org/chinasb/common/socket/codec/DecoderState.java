package org.chinasb.common.socket.codec;

/**
 * 解码器状态
 * @author zhujuan
 *
 */
public enum DecoderState {
    /**
     * 等待数据：解码过程中数据不完整，等待接收数据
     */
    WAITING_DATA,
    /**
     * 解码就绪：进入解码就绪状态，等待新的解码开始
     */
    READY;
}
