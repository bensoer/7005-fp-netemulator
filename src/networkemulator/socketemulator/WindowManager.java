package networkemulator.socketemulator;

import networkemulator.Logger;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Created by bensoer on 10/11/15.
 *
 * WindowManager acts as the window in the network emulator and the main storage container and manager for
 * unacknowledged packets
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

    /**
     * getInitialPAcketDelay gets the set packet delay for all packets used in this windowmanager.
     * @return int - the initial packet delay
     */
    public int getInitialPacketDelay(){
        return this.initPacketTimeout;
    }

    /**
     * determines based on the current window size and the max window size as to whether a packet can be added
     * @return boolean - the state of whether a packet can be added or not
     */
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
     * the old packet with the new one. If there is not room and the packet is not a retransmission, then nothing
     * will happen. The client is expected to use the appropriate canAddPacket() method to determine whether a packet
     * can be added
     * @param packet Packet - the packet attempting to be added to the window
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

    /**
     * findMatchingPacketIndex searches through the window for a packet that has the same sequence and acknowledgement
     * numbers and returns its index. If it does not exist it returns -1. This override will accept a PacketMEta object
     * and compare sequence and acknowledgement numbers of the packets in the window with the packet in the PacketMeta
     * object
     * @param packet PacketMeta - the PAcketMEta object containing the Packet to find a matching one in the window
     * @return int - the index in the window of the matching packet. returns -1 if there is not mathcing packet
     */
    private int findMatchingPacketIndex(PacketMeta packet){
        for(int i=0; i < window.size(); i++){

            if(window.get(i).packet.seqNum == packet.packet.seqNum && window.get(i).packet.ackNum == packet.packet.ackNum){
                return i;
            }
        }
        return -1;

    }

    /**
     * findMatchingPacketSent tries to find a matching packet that was sent before the passed in packet. This methods typical
     * use is to determine what packet an the passed in acknowledgement packet belongs to. Matching sent packet is
     * determined by the acknowledgement number from the passed in packet matching a sequence number of a packet in the
     * window
     * @param packet Packet - the packet to find the matching sent packet for.This should be an ACK packet
     * @return int - the index of the sent packet in the window. Returns -1 if there is not matching packet
     */
    private int findingMatchingSentPacket(Packet packet){
        for(int i=0; i < window.size(); i++){

            if(window.get(i).packet.seqNum == packet.ackNum){
                return i;
            }
        }
        return -1;
    }

    /**
     * findMatchingPacketIndex searches through the window for a packet that has the same sequence and acknowledgement
     * numbers and returns its index. If it does not exist it returns -1. This override will accept a Packet object
     * and compare sequence and acknowledgement numbers of the packets in the window with the packet in the PacketMeta
     * object
     * @param packet Packet - the packet to find a matching one in the window
     * @return int - the index in the window of the matching packet. returns -1 if there is not mathcing packet
     */
    private int findMatchingPacketIndex(Packet packet){
        for(int i=0; i < window.size(); i++){

            if(window.get(i).packet.seqNum == packet.seqNum && window.get(i).packet.ackNum == packet.ackNum){
                return i;
            }
        }
        return -1;
    }

    /**
     * acknowledgePacket acknowledges the passed in ACK packed and determines if there is a matching sent packet. If there
     * is a matching sent packet, its PacketMeta object is updated to state the packet has been acknowledged. If there is
     * not it is assumed this is a duplicate ACK from a packet that has been previously acknowledges already and removed
     * from the window.
     * @param packet PAcket - the ACK packet acknowledging the receipt of a sent packet
     */
    public void acknowledgePacket(Packet packet){
        int index = findingMatchingSentPacket(packet);

        if(index != -1){
            System.out.println("Packet Acknowledged and Found. Acknowledging Now");
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

    /**
     * attemptMoveWindow will try to slide the window forward as would happen in a typical TCP transfer. This is implemented
     * by starting at the beginning of the window and checking for acknowledged packets. If the packet is acknowledged
     * it is then remove from the window. As soon as an unacknowledged packet is hit or the end of the window is hit,
     * the move is aborted.
     */
    public void attemptMoveWindow(){
        boolean isMore = true;
        while(isMore){

            try{
                PacketMeta pm = window.firstElement();
                if(pm.acknowledged){
                    pop();
                }else{
                    isMore = false;
                }
            }catch(NoSuchElementException nsee){
                isMore = false;
            }
        }
    }

    /**
     * pop will remove the first element from the queue and return it
     * @return PacketMeta - the meta of the packet that was removed form the window. PAcketMEta contains the packet aswell
     */
    public PacketMeta pop(){
        PacketMeta packet = window.get(0);
        window.removeElementAt(0);
        return packet;
    }

    /**
     * getWindowSpace calculates the remaining window space in the WindowManager
     * @return int - the remaining window space in the window
     */
    public int getWindowSpace(){
        return windowSize - window.size();
    }






}
