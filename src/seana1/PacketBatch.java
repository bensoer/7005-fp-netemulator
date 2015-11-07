package seana1;

import java.io.Serializable;

/**
 * Created by bensoer on 06/11/15.
 */
public class PacketBatch implements Serializable {

    private Packet[] packets;
    private int lastIndex;

    public PacketBatch(int windowSize){
        packets = new Packet[windowSize];
        lastIndex = 0;
    }

    public void addPackets(Packet[] packets){
        for(Packet packet: packets){
            this.packets[lastIndex++] = packet;
        }
    }

    public void addPacket(Packet packet){
        this.packets[lastIndex++] = packet;
    }

    public Packet[] getPackets(){
        return this.packets;
    }

    public void removePacket(int index){
        if(index < packets.length){
            this.packets[index] = null;
        }
    }


}
