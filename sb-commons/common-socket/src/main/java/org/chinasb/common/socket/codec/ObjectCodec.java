package org.chinasb.common.socket.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.utility.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Input;
import flex.messaging.io.amf.Amf3Output;

/**
 * 对象编解码
 * 
 * @author zhujuan
 *
 */
public class ObjectCodec {
    private static final Log LOGGER = LogFactory.getLog(ObjectCodec.class);
    private static SerializationContext context = SerializationContext.getSerializationContext();

    /**
     * ByteArray -> ASObject
     * 
     * @param buffer
     * @return
     */
    public static Object byteArray2ASObject(byte[] buffer) {
        if ((buffer == null) || (buffer.length == 0)) {
            return null;
        }
        Amf3Input amfIn = new Amf3Input(context);
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        amfIn.setInputStream(is);
        Object obj = null;
        try {
            obj = amfIn.readObject();
        } catch (Exception e) {
            LOGGER.error("ByteArray2ASObject Error: " + e.getMessage());
        } finally {
            try {
                amfIn.close();
            } catch (Exception e) {
                LOGGER.error("Amf3Input.close() error: " + e.getMessage());
            }
            try {
                is.close();
            } catch (Exception e) {
                LOGGER.error("ByteArrayInputStream.close() error: " + e.getMessage());
            }
            is = null;
            amfIn = null;
        }
        return obj;
    }

    /**
     * ASObject -> ByteArray
     * 
     * @param obj
     * @return
     */
    public static byte[] asObject2ByteArray(Object obj) {
        if (obj == null) {
            return null;
        }
        Amf3Output amfOut = new Amf3Output(context);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        amfOut.setOutputStream(os);
        byte[] bytes = null;
        try {
            amfOut.writeObject(obj);
            os.flush();
            bytes = os.toByteArray();
        } catch (Exception ex) {
            LOGGER.error("Amf3Output.writeObject(obj) error: " + ex.getMessage());
        } finally {
            try {
                amfOut.close();
            } catch (Exception ex) {
                LOGGER.error("Amf3Output.close() error: " + ex.getMessage());
            }
            try {
                os.close();
            } catch (Exception e) {
                LOGGER.error("ByteArrayOutputStream.close() error: " + e.getMessage());
            }
            amfOut = null;
            os = null;
        }
        return bytes;
    }

    /**
     * Object -> ByteArray
     * 
     * @param obj
     * @return
     */
    public static byte[] object2ByteArray(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new ObjectOutputStream(bos).writeObject(obj);
            return bos.toByteArray();
        } catch (IOException ex) {
            LOGGER.error("failed to serialize obj", ex);
        }
        return null;
    }

    /**
     * ByteArray -> Object
     * 
     * @param buffer
     * @return
     */
    public static Object byteArray2Object(byte[] buffer) {
        if ((buffer == null) || (buffer.length == 0)) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception ex) {
            LOGGER.error("failed to deserialize obj", ex);
            return null;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
            }
            try {
                bais.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * ByteArray -> JsonNode
     * 
     * @param buffer
     * @return
     */
    public static Object byteArray2JsonNode(byte[] buffer) {
        if ((buffer == null) || (buffer.length == 0)) {
            return null;
        }
        try {
            return JsonUtils.getObjectMapper().readValue(buffer, JsonNode.class);
        } catch (Exception e) {
            LOGGER.error("byteArray2JsonNode Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * JsonObject -> ByteArray
     * 
     * @param obj
     * @return
     */
    public static byte[] jsonObject2ByteArray(Object obj) {
        if (obj == null) {
            return null;
        }

        byte[] bytes = null;
        try {
            bytes = JsonUtils.getWriter().writeValueAsBytes(obj);
        } catch (Exception ex) {
            LOGGER.error("jsonObject2ByteArray error: " + ex.getMessage());
        }
        return bytes;
    }
}
