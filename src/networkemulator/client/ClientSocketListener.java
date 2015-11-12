package networkemulator.client;

import networkemulator.*;

/**
 * Created by bensoer on 10/11/15.
 */
public class ClientSocketListener extends Thread {


    private TCPEngine socket;
    private WindowManager wm;

    public ClientSocketListener(TCPEngine socket, WindowManager wm){
        this.socket = socket;
        this.wm = wm;
    }

    @Override
    public void run(){


        while(true){
            Packet data = socket.readFromSocket();

            Logger.log("ClientSocketListener - Recieved A Packet: Seq: " + data.seqNum + " Ack: " +data.ackNum
                    + " PacketType: " + data.packetType + " Sender: [" + data.src + "] Recipient: [" + data.dst
                    + "] WindowSize: " + data.windowSize);
            if(data.packetType == PacketType.ACK.toInt()){
                Logger.log("ClientSocketListener - The Packet is an ACK. Checking/Updating Window");
                wm.acknowledgePacket(data);
                wm.attemptMoveWindow();
            }
        }

    }
}
