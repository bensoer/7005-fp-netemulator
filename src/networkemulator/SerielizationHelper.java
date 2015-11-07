package networkemulator;

import java.io.*;

/**
 * Created by bensoer on 06/11/15.
 */
public class SerielizationHelper {

    public static String toString(Serializable object) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream so = new ObjectOutputStream(bo);
        so.writeObject(object);
        so.flush();
        return bo.toString();
    }

    public static Object toObject(String serializedObject) throws IOException, ClassNotFoundException{
        byte b[] = serializedObject.getBytes();
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si = new ObjectInputStream(bi);
        return si.readObject();
    }
}
