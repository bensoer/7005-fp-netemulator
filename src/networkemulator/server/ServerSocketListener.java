package networkemulator.server;

import networkemulator.*;
import networkemulator.socketemulator.*;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bensoer on 11/11/15.
 *
 * ServerSocketListener is in charge of listening to incoming packets for the Server.
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

    /**
     * The default main entrance for the Thread. Creates an infinite while loop and waits for content to arrive, which
     * it then parses and decides what to do based on the type of packet recieved. When an EOT packet is recieved scanning
     * is done to make sure all packets have been recieved. At which point the Server switches roles as the reciever and
     * starts the ServerSocketSender thread to start sending data back to the client. The ServerSocketListener then listens
     * only for acknowledgements for the ServerSocketSender's window
     */
    @Override
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
                Logger.log("ServerSocketListener - Recieved an EOT packet. We could send this now");
                Packet acknowledgement = this.pb.createResponsePacket(data, "");
                PacketBuilder.sendPacket(acknowledgement, socket, wm);
                //add EOT to the list so that it is the appropriate length
                da.addData(data);
                da.EOTArrived();
            }else{
                Logger.log("ServerSocketListener - An Unknown Packet Type Was Recieved");
            }

            //if the EOT has occurred and we have recieved all missing packets. LETS GOO
            if(!da.isDisabled() && da.EOTHasArrived() && !da.isMissingPackets(0)){
                Logger.log("ServerSocketListener - EOT Received and And No Missing Packets");
                String fileContent = da.fetchData();

                Logger.log("ServerSocketListener - File retrieved. Now Writing");

                try{
                    FileWriter fw = new FileWriter("./files/server/300loriumipsum.txt");
                    fw.write(fileContent);
                    fw.flush();
                    fw.close();
                }catch(IOException ioe){
                    Logger.log("ServerSocketListener - Writing Retrieved To File");
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
