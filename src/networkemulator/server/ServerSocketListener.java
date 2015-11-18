package networkemulator.server;

import networkemulator.*;
import networkemulator.socketemulator.*;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bensoer on 11/11/15.
 */
public class ServerSocketListener extends Thread {


    private TCPEngine socket;
    private WindowManager wm;
    private PacketBuilder pb;
    private Thread senderThread;

    public ServerSocketListener(TCPEngine socket, WindowManager wm, Thread senderThread){
        this.socket = socket;
        this.wm = wm;
        this.pb = new PacketBuilder(Locations.SERVER, Locations.CLIENT, this.wm);
        this.senderThread = senderThread;
    }


    public void run(){
        ConfigurationManager cm = ConfigurationManager.getInstance();
        DataAssembler da = new DataAssembler(cm.serverPacketMaxSize);
        while(true){
            Packet data = socket.readFromSocket();
            Logger.log("ServerSocketListener - Recieved A Packet: Seq: " + data.seqNum + " Ack: " + data.ackNum
                    + " PacketType: " + data.packetType + " Sender: [" + data.src + "] Recipient: [" + data.dst
                    + "] WindowSize: " + data.windowSize);

            if(data.packetType == PacketType.ACK.toInt()){
                Logger.log("ServerSocketListener - It is an ACK packet");
                wm.acknowledgePacket(data);
                wm.attemptMoveWindow();
            }else if(data.packetType == PacketType.PUSH.toInt()){
                Packet acknowledgement = this.pb.createResponsePacket(data, "");
                PacketBuilder.sendPacket(acknowledgement, socket, wm);
                da.addData(data);

            }else if(data.packetType == PacketType.EOT.toInt()){
                Logger.log("ServerSocketListener - Transmission has terminated. We could send stuff now");
                Packet acknowledgement = this.pb.createResponsePacket(data, "");
                PacketBuilder.sendPacket(acknowledgement, socket, wm);
                da.EOTArrived();
            }else{
                Logger.log("ServerSocketListener - An Unknown Packet Type Was Recieved");
            }

            //if the EOT has occurred and we have recieved all missing packets. LETS GOO
            if(!da.isDisabled() && da.EOTHasArrived() && !da.isMissingPackets()){
                Logger.log("ServerSocketListener - EOT Received and And No Missing Packets");
                String fileContent = da.fetchData();

                System.out.println("ServerSocketListener - File retrieved. Now Writing");

                try{
                    FileWriter fw = new FileWriter("./files/server/300loriumipsum.txt");
                    fw.write(fileContent);
                    fw.flush();
                    fw.close();
                }catch(IOException ioe){
                    System.out.println("ServerSocketListener - Writing Retrieved To File");
                    ioe.printStackTrace();
                }

                Logger.log("ServerSocketListener -- ATTENTION -- Transition Is Occurring. Client Will Now Recieve" +
                        " data from the Server");

                da.setDisableState(true);
                this.senderThread.start();

            }
        }

    }


}
