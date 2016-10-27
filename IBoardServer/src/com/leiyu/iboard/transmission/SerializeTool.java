package com.leiyu.iboard.transmission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by leiyu on 2016/10/25.
 */

public class SerializeTool {

    public static String object2String(Object o) {
        String objBody = null;
        ByteArrayOutputStream baops = null;
        ObjectOutputStream oos = null;

        try {
            baops = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baops);
            oos.writeObject(o);
            byte[] bytes = baops.toByteArray();
            objBody = new String(bytes);
        } catch (Exception e) {

        } finally {
            try {
                oos.close();
            } catch (Exception e1) {}

            try {
                baops.close();
            } catch (Exception e1) {}
        }

        return objBody;
    }

    public static <T extends Serializable> T getObjectFromString(String objBody, Class<T> clazz) {
        byte[] bytes = objBody.getBytes();
        ObjectInputStream ois = null;
        T obj = null;

        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            obj = (T) ois.readObject();
        } catch (Exception e) {}
        finally {
            try {
                ois.close();
            } catch (Exception e1) {}
        }

        return obj;
    }
}
