package networkemulator.server;

import networkemulator.*;

/**
 * Created by bensoer on 11/11/15.
 */
public class ServerSocketListener extends Thread {


    private TCPEngine socket;
    private WindowManager wm;
    private PacketBuilder pb;

    public ServerSocketListener(TCPEngine socket, WindowManager wm){
        this.socket = socket;
        this.wm = wm;
        this.pb = new PacketBuilder(Locations.SERVER, Locations.CLIENT, this.wm);
    }


    public void run(){
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
            }else if(data.packetType == PacketType.EOT.toInt()){
                Logger.log("ServerSocketListener - Transmission has terminated. We can send stuff now");
                Packet acknowledgement = this.pb.createResponsePacket(data, "");
                PacketBuilder.sendPacket(acknowledgement, socket, wm);
            }else{
                Logger.log("ServerSocketListener - An Unknown Packet Type Was Recieved");
            }
        }

    }
}
