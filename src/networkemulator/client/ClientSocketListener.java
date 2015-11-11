package networkemulator.client;

import networkemulator.Packet;
import networkemulator.PacketType;
import networkemulator.TCPEngine;
import networkemulator.WindowManager;

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

            if(data.packetType == PacketType.ACK.toInt()){
                wm.acknowledgePacket(data);
                wm.attemptMoveWindow();
            }
        }

    }
}
