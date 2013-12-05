package org.chinasb.common.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Input;
import flex.messaging.io.amf.Amf3Output;

/**
 * 对象转换工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class ObjectConverterUitls {
    
    private static final Log log = LogFactory.getLog(ObjectConverterUitls.class);

    private static SerializationContext context = SerializationContext.getSerializationContext();

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
            log.error("ByteArray2ASObject Error: " + e.getMessage());
        } finally {
            try {
                amfIn.close();
            } catch (Exception e) {
                log.error("Amf3Input.close() error: " + e.getMessage());
            }
            try {
                is.close();
            } catch (Exception e) {
                log.error("ByteArrayInputStream.close() error: " + e.getMessage());
            }
            is = null;
            amfIn = null;
        }

        return obj;
    }

    public static byte[] asObject2ByteArray(Object obj) {
        if (obj == null) {
            return null;
        }

        Amf3Output amfOut = new Amf3Output(context);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        amfOut.setOutputStream(os);

        byte[] bytes = (byte[]) null;
        try {
            amfOut.writeObject(obj);
            os.flush();
            bytes = os.toByteArray();
        } catch (Exception ex) {
            log.error("Amf3Output.writeObject(obj) error: " + ex.getMessage());
        } finally {
            try {
                amfOut.close();
            } catch (Exception ex) {
                log.error("Amf3Output.close() error: " + ex.getMessage());
            }

            try {
                os.close();
            } catch (Exception e) {
                log.error("ByteArrayOutputStream.close() error: " + e.getMessage());
            }
            amfOut = null;
            os = null;
        }

        return bytes;
    }

    public static byte[] object2ByteArray(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new ObjectOutputStream(bos).writeObject(obj);

            return bos.toByteArray();
        } catch (IOException ex) {
            log.error("failed to serialize obj", ex);
        }
        return null;
    }

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
            log.error("failed to deserialize obj", ex);
            return null;
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (Exception localException5) {}
            try {
                bais.close();
            } catch (Exception localException6) {}
        }
    }
}
