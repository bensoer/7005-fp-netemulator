package networkemulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bensoer on 16/11/15.
 */
public class DataAssembler {

    private List<Packet> dataSequence = new ArrayList<Packet>();
    private int packetSize;

    private boolean eotArrived = false;
    private boolean disabled = false;

    public DataAssembler(int packetSize){
        this.packetSize = packetSize;
    }

    public void addData(Packet packet){
        System.out.println("DataAssembler - Adding Packet");
        ConfigurationManager cm = ConfigurationManager.getInstance();

        int index = packet.seqNum / packetSize;

        if(index >= dataSequence.size()){

            while(dataSequence.size() <= index){
                dataSequence.add(null);
            }
        }
        dataSequence.set(index, packet);

    }

    public String fetchData(){

        String fullData = "";
        for(Packet packet: dataSequence){
            fullData += packet.data;
        }

        return fullData;

    }

    public void clearAssemblyCache(){
        this.dataSequence = new ArrayList<Packet>();
    }

    public void EOTArrived(){
        System.out.println("DataAssembler - EOT HAS ARRIVED");
        this.eotArrived = true;
    }

    public void setDisableState(boolean state){
        this.disabled = state;
    }

    public boolean isDisabled(){
        return this.disabled;
    }

    public boolean EOTHasArrived(){
        System.out.println("DataAssembler - Returning Status of EOT. It is: " + this.eotArrived);
        return this.eotArrived;
    }

    public boolean isMissingPackets(){
        int i = 0;
        System.out.println("DataAssembler - Checking For Missing Packets");
        for(Packet packet: dataSequence){
            ++i;
            if(packet == null){
                System.out.println("DataAssembler - Found Missing Packet");
                return true;
            }
        }

        return false;
    }

}
