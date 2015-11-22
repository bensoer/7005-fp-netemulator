package networkemulator.client;

import networkemulator.*;
import networkemulator.socketemulator.*;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bensoer on 10/11/15.
 *
 * ClientSocketListener is a thread that listens for incoming data for the Client. This is used to listen for
 * acknowledgements of packets and listen for incoming recieval of packets from the server
 */
public class ClientSocketListener extends Thread {


    private TCPEngine socket;
    private WindowManager wm;
    private PacketBuilder pb;

    public ClientSocketListener(TCPEngine socket, WindowManager wm){
        this.socket = socket;
        this.wm = wm;
        this.pb = new PacketBuilder(Locations.CLIENT, Locations.SERVER, this.wm);
    }

    /**
     * the main starting function for the thread. This creates an infinite while loop where it listens for packets,
     * parses what type they are, and then carries out the appropriate action based on that type
     */
    @Override
    public void run(){
        ConfigurationManager cm = ConfigurationManager.getInstance();
        DataAssembler da = new DataAssembler(cm.serverPacketMaxSize);
        while(true){
            //System.out.println("ClientSocketListener - About to Read From Socket");
            Packet data = socket.readFromSocket();
            //System.out.println("ClientSocketListener - Recieved From Socket");

            Logger.log("ClientSocketListener - Recieved A Packet: Seq: " + data.seqNum + " Ack: " +data.ackNum
                    + " PacketType: " + data.packetType + " Sender: [" + data.src + "] Recipient: [" + data.dst
                    + "] WindowSize: " + data.windowSize);
            if(data.packetType == PacketType.ACK.toInt()){
                Logger.log("ClientSocketListener - The Packet is an ACK. Checking/Updating Window");
                wm.acknowledgePacket(data);
                wm.attemptMoveWindow();
            }else if(data.packetType == PacketType.EOT.toInt()){
                Logger.log("ClientSocketListener - Transmission has terminated. We could send stuff now");
                Packet acknowledgement = this.pb.createResponsePacket(data, "");
                PacketBuilder.sendPacket(acknowledgement, socket, wm);
                //add the EOT packet so that the list is appropriatly set
                da.addData(data);
                da.EOTArrived();
            }else if(data.packetType == PacketType.PUSH.toInt()){
                Logger.log("ClientSocketListener - It is a PUSH packet. Sending back an ACK");
                Packet acknowledgement = this.pb.createResponsePacket(data, "");
                PacketBuilder.sendPacket(acknowledgement, socket, wm);
                da.addData(data);
            }else{
                Logger.log("ClientSocketListener - An unknown Packet was Recieved");
                //just to get rid of duplicate code prompt
                Logger.log("");
            }

            //if the EOT has occurred and we have recieved all missing packets. LETS GOO
            if(!da.isDisabled() && da.EOTHasArrived() && !da.isMissingPackets(2)){
                Logger.log("ClientSocketListener - EOT Received and And No Missing Packets");
                String fileContent = da.fetchData();

                Logger.log("ClientSocketListener - File retrieved. Now Writing");

                try{
                    FileWriter fw = new FileWriter("./files/client/serveripsum.txt");
                    fw.write(fileContent);
                    fw.flush();
                    fw.close();
                }catch(IOException ioe){
                    Logger.log("ClientSocketListener - Writing To File Failed");
                    ioe.printStackTrace();
                }

            }
        }

    }
}
