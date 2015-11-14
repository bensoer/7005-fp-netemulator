package networkemulator;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Created by bensoer on 10/11/15.
 */
public class WindowManager {

    private Vector<PacketMeta> window;
    private int windowSize;

    private boolean vectorIsBeingEdited = false;
    private int initPacketTimeout;

    public int previousSEQ = 0;
    public int previousACK = 500;


    public WindowManager(int windowSize, int packetInitialTimeoutLength){
        window = new Vector<PacketMeta>(windowSize);
        this.windowSize = windowSize;
        this.initPacketTimeout = packetInitialTimeoutLength;
    }

    public int getInitialPacketDelay(){
        return this.initPacketTimeout;
    }

    public boolean canAddPacket(){
        System.out.println("WindowManager - The current Window Size is : " + windowSize + ". The number of slots taken are: " + window.size());
        return window.size() < windowSize;
    }

    /**
     * canAddPacket checks whether the passed packet is in the window already. If it is in the window already this infers
     * the client is triggering a retransmission, so it can go through. Otherwise if it is not, it can only be added if
     * there is room in the window
     * @param packet Packet - the packet attempting to be added to the window
     * @return Boolean - whether or not the packet can be added to the window
     */
    public boolean canAddPacket(Packet packet){
        System.out.println("WindowManager - The current Window Size is : " + windowSize + ". The number of slots taken are: " + window.size());

        int index = findMatchingPacketIndex(packet);
        if(index != -1){
            return true;
        }else{
            return window.size() < windowSize;
        }
    }

    /**
     * push will push on a packet at the end of the window unless it already exists in which case it will replace
     * the old packet with the new one
     * @param packet
     */
    public void push(PacketMeta packet){
        if(window.size() < windowSize || findMatchingPacketIndex(packet) != -1) {
            Logger.log("WindowManager - Window Has Room. Size: " + windowSize + ". Slots taken: " + window.size());


            //check if one already exists and replace it
            int index = findMatchingPacketIndex(packet);
            if(index != -1){

                Logger.log("WindowManager - Found Duplicate Packet in the Window. Overwriting...");
                //update the PacketMeta send count to be whatever its last value + 1
                packet.setsendCount((window.get(index).getsendCount() + 1));

                window.set(index, packet);
            }else{
                //else add the packet to the end

                //update the PacketMeta that this is the first time it is being sent
                packet.setsendCount(1);
                window.add(packet);
            }

            Logger.log("WindowManager - Window Has Now Had Packet Added. Size: " + windowSize + ". Slots taken: " + window.size());
        }else{
            Logger.log("WindowManager - Push Attempted By Program but Window Has No Room. Size: " + windowSize + ". Slots taken: " + window.size());
            Logger.log("WindowManager - The Packet with Seq " + packet.packet.seqNum + " was not added becuase of this");
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

    private int findingMatchingSentPacket(Packet packet){
        for(int i=0; i < window.size(); i++){

            if(window.get(i).packet.ackNum == packet.seqNum){
                return i;
            }
        }
        return -1;
    }

    private int findMatchingPacketIndex(Packet packet){
        for(int i=0; i < window.size(); i++){

            if(window.get(i).packet.seqNum == packet.seqNum && window.get(i).packet.ackNum == packet.ackNum){
                return i;
            }
        }
        return -1;
    }

    public void acknowledgePacket(Packet packet){
        int index = findingMatchingSentPacket(packet);

        if(index != -1){
            PacketMeta pm = window.get(index);
            pm.acknowledged = true;
            window.set(index, pm);

            //recalculate the ACK number for future requests made from this side
            int newACKCandidate = pm.packet.seqNum + pm.packet.data.length();
            if(newACKCandidate > this.previousACK){
                this.previousACK = newACKCandidate;
            }

        }else{
            //DUPLICATE ACK. SAY WURT
            if(packet.packetType == PacketType.ACK.toInt()){
                Logger.log("WindowManager - A Duplicate ACK has been received. Seq:" + packet.seqNum + " Ack: " + packet.ackNum
                        + " Type: " + packet.packetType + " Src: [" + packet.src + "] Dst: [" + packet.dst + "]  WindowSize: "
                        + packet.windowSize);
                Logger.log("WindowManager - A Duplicate ACK signals that your timer preferences settings are too short "
                        + "and should be extended. For this Emulation, the duplicate ACK will simply be dropped");
            }

            //otherwise not sure what came back so far...

        }
    }

    public void attemptMoveWindow(){

        boolean isMore = true;
        while(isMore){


            try{
                PacketMeta pm = window.firstElement();
                if(pm.acknowledged){
                    pop();
                }
            }catch(NoSuchElementException nsee){
                isMore = false;
            }
        }
    }

    private int releaseWindowAccess(){
        vectorIsBeingEdited = false;
        return 1;
    }

    private int requestWindowAccess(){
        while(vectorIsBeingEdited){
            try{
                Thread.sleep(1);
            }catch(InterruptedException ie){
                Logger.log("WindowManager - There was an error sleeping requesitng for the Vector to be available");
            }
        }
        vectorIsBeingEdited = true;

        return 1;
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
