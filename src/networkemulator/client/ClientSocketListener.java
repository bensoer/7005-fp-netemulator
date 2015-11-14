package networkemulator.client;

import networkemulator.*;

/**
 * Created by bensoer on 10/11/15.
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

    @Override
    public void run(){


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
                Logger.log("ClientSocketListener - Transmission has terminated. We can send stuff now");
            }else if(data.packetType == PacketType.PUSH.toInt()){
                Logger.log("ClientSocketListener - It is a PUSH packet. Sending back an ACK");
                Packet acknowledgement = this.pb.createResponsePacket(data, "");
                PacketBuilder.sendPacket(acknowledgement, socket, wm);
            }else{
                Logger.log("ClientSocketListener - An unknown Packet was Recieved");
                //just to get rid of duplicate code prompt
                Logger.log("");
            }
        }

    }
}
