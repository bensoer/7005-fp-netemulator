package seana1;

import java.io.Serializable;

/**
 * Created by bensoer on 06/11/15.
 */
public class Packet implements Serializable {

        public int packetType;
        public int seqNum;
        public String data;
        public int windowSize;
        public int ackNum;
}