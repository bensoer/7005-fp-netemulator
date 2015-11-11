package networkemulator.internet;

import networkemulator.Locations;
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
            System.out.println("Server2Internet - Client is Sending Data. Listening to Client");
            data = sender.readFromSocket();
            System.out.println("Back from read");
            //System.out.println(data);

            if(InternetTools.dropPacket(50)){
                System.out.println("Server2Internet - Packet with Seq: " + data.seqNum + " is being dropped");
                continue;
            }else{

                System.out.println("Server2Internet - Sending Data");

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
