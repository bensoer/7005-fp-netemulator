package networkemulator.internet;

import networkemulator.Locations;
import networkemulator.Logger;
import networkemulator.Packet;
import networkemulator.TCPEngine;

import java.io.IOException;

/**
 * Created by bensoer on 10/11/15.
 */
public class Server2Internet extends Thread {

    private TCPEngine sender;
    private TCPEngine listener;


    public Server2Internet(TCPEngine sender, TCPEngine listener){
        this.sender = sender;
        this.listener = listener;
    }

    @Override
    public void run(){

        Packet data;
        while(true){

            //means the client is sending data to the server
            System.out.println("Server2Internet - Server is Sending Data. Listening to Server");
            data = sender.readFromSocket();
            System.out.println("Server2Internet - Back from read");
            //System.out.println(data);
            Logger.log("Server2Internet - Received Packet Seq: " + data.seqNum + " Ack: " + data.ackNum
                    + " Src: [" + data.src + "] Dst: [" + data.dst + "] Type: " + data.packetType + " WindowSize: "
                    + data.windowSize);

            if(InternetTools.dropPacket(0)){
                System.out.println("Server2Internet - Packet with Seq: " + data.seqNum + " is being dropped");
                continue;
            }else{

                System.out.println("Server2Internet - Sending Packet with Seq: " + data.seqNum);

                if(data.dst.equals(Locations.CLIENT.toString())){

                    //means server is sending to the client
                    System.out.println("Server2Internet - Sending Data to Client");
                    listener.writeToSocket(data);

                }else if(data.dst.equals(Locations.SERVER.toString())){

                    //means this packet is for the server
                    System.out.println("Server2Internet - Sending Data to Server");
                    sender.writeToSocket(data);

                }
            }

        }
    }
}
