package seana1;

import java.io.*;
import java.util.Base64;

/**
 * Created by bensoer on 06/11/15.
 */
public class SerielizationHelper {

    public static String toString(Serializable object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( object );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static Object toObject(String serializedObject) throws IOException, ClassNotFoundException{
        byte [] data = Base64.getDecoder().decode( serializedObject );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }
}
