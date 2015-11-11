package networkemulator;

import java.util.Vector;

/**
 * Created by bensoer on 10/11/15.
 */
public class WindowManager {

    private Vector<PacketMeta> window;
    private int windowSize;


    public WindowManager(int windowSize){
        window = new Vector<PacketMeta>(windowSize);
        this.windowSize = windowSize;
    }

    public boolean canAddPacket(){
        System.out.println("WindowManager - canAddPacket Breakdown: \n\t Capacity: " + window.capacity() + " \n\t windowSize: " + windowSize + " \n\t Size: " + window.size());
        return window.size() < windowSize;
    }

    /**
     * push will push on a packet at the end of the window unless it already exists in which case it will replace
     * the old packet with the new one
     * @param packet
     */
    public void push(PacketMeta packet){
        System.out.println("WindowManager - Pushing Packet: \n\t Capacity: " + window.capacity() + " \n\t windowSize: " + windowSize + "\n\t Size: " + window.size());
        if(window.size() < windowSize) {

            //check if one already exists and replace it
            int index = findMatchingPacketIndex(packet);
            if(index != -1){
                window.remove(index);
                window.add(index, packet);
            }else{
                //else add the packet
                window.add(packet);
            }
        }
    }

    private int findMatchingPacketIndex(PacketMeta packet){
        for(int i=0; i < window.size(); i++){

            if(window.get(i).packet.seqNum == packet.packet.seqNum && window.get(i).packet.ackNum == packet.packet.ackNum){
                return i;
            }
        }
        return -1;
    }

    private int findMatchingPacketIndex(Packet packet){
        PacketMeta pm = new PacketMeta(null,null,null);
        pm.packet = packet;
        return findMatchingPacketIndex(pm);
    }

    public void acknowledgePacket(Packet packet){
        int index = findMatchingPacketIndex(packet);

        if(index != -1){
            PacketMeta pm = window.get(index);
            pm.acknowledged = true;
            window.remove(index);
            window.add(index, pm);
        }else{
            //DUPLICATE ACK. SAY WURT
            System.out.println("For some unexplainable gawdamn reason you just got a duplicate ack...");
        }
    }

    public void attemptMoveWindow(){

        boolean isMore = true;
        while(isMore){

            PacketMeta pm = window.firstElement();
            if(pm.acknowledged){
                pop();
            }else{
                isMore = false;
            }
        }
    }

    /**
     * pop will remove the first element from the queue and return it
     * @return
     */
    public PacketMeta pop(){
        PacketMeta packet = window.get(0);
        window.removeElementAt(0);
        return packet;
    }

    public int getWindowSpace(){
        return windowSize - window.size();
    }






}
