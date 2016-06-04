package org.chinasb.common.socket.codec;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.socket.message.Response;
import org.chinasb.common.socket.type.ResponseCode;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 返回消息编码器
 * 
 * @author zhujuan
 *
 */
@Component
public class ResponseEncoder extends MessageToByteEncoder<Object> {

    private static final Log LOGGER = LogFactory.getLog(RequestDecoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        if (message == null) {
            return;
        }

        if (message instanceof ByteBuf) {
            out.writeBytes((ByteBuf) message);
        } else if (message instanceof byte[]) {
            byte[] bytes = (byte[]) message;
            out.writeBytes(bytes);
        } else {
            ByteBuf buf = transform(message);
            if (buf != null) {
                out.writeBytes(buf);
            }
        }
    }

    /**
     * 消息编码
     * 
     * @param message 消息对象
     * @return
     */
    public ByteBuf transform(Object message) {
        return transformByteArray(encodeResponse(message));
    }

    /**
     * 消息编码
     * 
     * @param bytes 消息字节数据
     * @return
     */
    public ByteBuf transformByteArray(byte[] bytes) {
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        int messageLength = bytes.length;
        ByteBuf bytebuf = Unpooled.buffer(messageLength + RequestDecoder.PACKAGE_HEADER_LEN);
        bytebuf.writeInt(RequestDecoder.PACKAGE_HEADER_ID);
        bytebuf.writeInt(messageLength);
        bytebuf.writeBytes(bytes);
        return bytebuf;
    }

    /**
     * 消息对象转换成字节数组
     * 
     * @param message 消息对象
     * @return
     */
    public byte[] encodeResponse(Object message) {
        if (message instanceof Response) {
            Response response = (Response) message;
            response.setTime(System.currentTimeMillis());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                int sn = response.getSn();
                int messageType = response.getMessageType();
                int module = response.getModule();
                int cmd = response.getCmd();

                dataOutputStream.writeInt(sn);
                dataOutputStream.writeShort(module);
                dataOutputStream.writeShort(cmd);
                dataOutputStream.writeByte(messageType);
                dataOutputStream.writeLong(response.getTime());

				Object value = response.getValue();
				if (value != null) {
					byte[] bytes = transferByteArray(messageType, value);
					if (bytes == null) {
						response.setStatus(ResponseCode.RESPONSE_CODE_ERROR);
						dataOutputStream.writeInt(response.getStatus());
					} else {
						dataOutputStream.writeInt(response.getStatus());
						dataOutputStream.write(bytes);
					}
				} else {
					dataOutputStream.writeInt(response.getStatus());
				}
                return byteArrayOutputStream.toByteArray();
            } catch (Exception ex) {
                LOGGER.error("ERROR", ex);
            } finally {
                try {
                    dataOutputStream.close();
                } catch (IOException ex) {
                    LOGGER.error("ERROR", ex);
                }
                try {
                    byteArrayOutputStream.close();
                } catch (IOException ex) {
                    LOGGER.error("ERROR", ex);
                }
                dataOutputStream = null;
                byteArrayOutputStream = null;
            }
        }
        return null;
    }

    /**
     * 转换消息字节数据
     * 
     * @param messageType 消息类型
     * @param obj 消息内容
     * @return
     */
    protected byte[] transferByteArray(int messageType, Object obj) {
        if (messageType == MessageType.AMF3.ordinal()) {
            return ObjectCodec.asObject2ByteArray(obj);
        }
        if (messageType == MessageType.JAVA.ordinal()) {
            return ObjectCodec.object2ByteArray(obj);
        }
        if (messageType == MessageType.JSON.ordinal()) {
            return ObjectCodec.jsonObject2ByteArray(obj);
        }
        return null;
    }

}
