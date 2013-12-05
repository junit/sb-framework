package org.chinasb.common.utility;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * XML序列化工具
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class XmlSerializerUtils {
    
    /**
     * 序列化
     * @param list
     * @param fos
     * @throws IOException
     */
    public static void write(Collection<Object> list, OutputStream fos) throws IOException {
        if (list != null && !list.isEmpty()) {
            XMLEncoder encoder = new XMLEncoder(fos);
            for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                Object obj = it.next();
                encoder.writeObject(obj);
            }
            encoder.flush();
            encoder.close();
        }
        fos.close();
    }

    /**
     * 反序列化
     * @param fis
     * @return
     * @throws IOException
     */
    public static List<Object> read(InputStream fis) throws IOException {
        List<Object> objList = new ArrayList<Object>();
        XMLDecoder decoder = new XMLDecoder(fis);
        Object obj = decoder.readObject();
        while (obj != null) {
            objList.add(obj);
            obj = decoder.readObject();
        }
        decoder.close();
        fis.close();
        return objList;
    }
}
