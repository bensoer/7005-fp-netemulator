package networkemulator;

import networkemulator.socketemulator.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bensoer on 16/11/15.
 *
 * DataAssembler stores passed in packets and allows the client to be able to determine when a complete set of data
 * has been recieved in packets. The DataAssember stores its data in packets, and only when the client wants to fetch
 * all of its data does the DataAssembler fetch and append the data in each packet
 */
public class DataAssembler {

    private List<Packet> dataSequence = new ArrayList<Packet>();
    private int packetSize;

    private boolean eotArrived = false;
    private boolean disabled = false;
    private int offset = 0;

    public DataAssembler(int packetSize){
        this.packetSize = packetSize;
    }

    /**
     * setOffset sets the offset value of the initial sequence number. This is so that when adding packets, the first
     * packet sent will always end up in index 0. Allowing the data assembler to accuratly calculate if there are any
     * missing packets
     * @param offset int - the offset amount
     */
    public void setOffset(int offset){
        this.offset = offset;
    }

    /**
     * addData adds the passed in packet into the dataSequence which is the store of packets. To be able to determine
     * ordering of the packets addData uses the set packet size and sequence number to determine the index in its storage
     * the packet belongs. If the index does not exist yet, null spots are added until the index is available. If the index
     * is available the passed in packet is added to that spot. Note if a previous packet was placed in that index, it will
     * be overwritten if this method is called again with another packet
     * @param packet Packet - The packet beign added to the assembler storage
     */
    public void addData(Packet packet){
        System.out.println("DataAssembler - Adding Packet");

        int index = (packet.seqNum-this.offset) / packetSize;

        if(index >= dataSequence.size()){

            while(dataSequence.size() <= index){
                dataSequence.add(null);
            }
        }
        dataSequence.set(index, packet);

    }

    /**
     * fetchData grabs all non-null packets in its storage and appends them in the order they were placed in the
     * assembler. Note that any null packets are simply skipped. To determine if there are any missing packets call the
     * isMissingPAckets() method first
     * @return String - the appended together data in the data assembler
     */
    public String fetchData(){

        String fullData = "";
        for(Packet packet: dataSequence){
            if(packet != null){
                fullData += packet.data;
            }
        }

        return fullData;

    }

    /**
     * clearAssemblyCache clears out all contents in the storage
     */
    public void clearAssemblyCache(){
        this.dataSequence = new ArrayList<Packet>();
    }

    /**
     * triggers that the EOT has arrived. This is meant to be a helper for the client so as to prepare that all data
     * may hve been recieved at this point or later
     */
    public void EOTArrived(){
        System.out.println("DataAssembler - EOT HAS ARRIVED");
        this.eotArrived = true;
    }

    /**
     * sets the state of whether the data assembler is disabled or not. This is not enforced in any functions and is
     * used as a helper for the client to control whether to be adding more data to the assembler or nhot
     * @param state boolean - the state of whether the DataAssembler is disabled or not
     */
    public void setDisableState(boolean state){
        this.disabled = state;
    }

    /**
     * isDisabled returns the state of whether the dataAssembler is disabled or not. This is not enforced in any functions
     * @return boolean - the state of whether the DataAssembler is disabled or not
     */
    public boolean isDisabled(){
        return this.disabled;
    }

    /**
     * EOTHasArrived is used to determine if the DataAssembler has recieved an EOT packet or not
     * @return boolean - the state of whether an EOT packet has been recieved or not
     */
    public boolean EOTHasArrived(){
        System.out.println("DataAssembler - Returning Status of EOT. It is: " + this.eotArrived);
        return this.eotArrived;
    }

    /**
     * isMissingPacket determines from the passed in starting index and later if there are any missing packets. This is a
     * short-circuited check that runs through the indexes starting at the passed in value until it hits a null value at
     * which point it will return true that there are missing packets. Because of sequence numbers not starting at 0,
     * the starting index is not gauranteed to be 0. So a passed in value is used to determine where the start is. The
     * end of the data is determined by the current size of the storage at the time of calling the isMissingPackets
     * method
     * @param startIndex int - the start index to start searching if there are missing packets at
     * @return boolean - the state of whether there are missing packets or not
     */
    public boolean isMissingPackets(int startIndex){
        System.out.println("DataAssembler - Checking For Missing Packets");
        for(int i = startIndex; i < dataSequence.size(); i++){
            if(dataSequence.get(i) == null){
                System.out.println("DataAssembler - Found Missing Packet");
                System.out.println("Missing Packet: Index: " + i + " Seq: " + (i*200 + 500));
                return true;
            }
        }
        return false;
    }

}
