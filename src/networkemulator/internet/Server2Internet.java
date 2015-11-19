package networkemulator.internet;

import networkemulator.socketemulator.Locations;
import networkemulator.Logger;
import networkemulator.socketemulator.Packet;
import networkemulator.socketemulator.TCPEngine;

/**
 * Created by bensoer on 10/11/15.
 *
 * Server2Internet listens for incoming data from the server. Initialy this will be all ACK data as the client sends data,
 * and this class will decide whether to drop or how long to delay the ACK using the InternetTools class. When the Server
 * is sending data this will be listening for data packets to send to the client
 */
public class Server2Internet extends Thread {

    private TCPEngine sender;
    private TCPEngine listener;

    private int bitErrorPercent;


    public Server2Internet(TCPEngine sender, TCPEngine listener, int bitErrorPercent){
        this.sender = sender;
        this.listener = listener;
        this.bitErrorPercent = bitErrorPercent;
    }

    /**
     * the main entrance point for the thread. Creates an infinite while loop where it listens for data, then passes it
     * to the InternetTools to determine whether to drop the packet. If it is not being dropped, then using the InternetTools
     * class determines how long to delay the class. The loop uses the packets 'src' and 'dst' attributes to determine
     * what socket to pass the packet to when sending it
     */
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

            if(InternetTools.dropPacket(this.bitErrorPercent)){
                System.out.println("Server2Internet - Packet with Seq: " + data.seqNum + " is being dropped");
                continue;
            }else{

                System.out.println("Server2Internet - Sending Packet with Seq: " + data.seqNum);

                if(data.dst.equals(Locations.CLIENT.toString())){

                    //means server is sending to the client
                    System.out.println("Server2Internet - Sending Data to Client");
                    //listener.writeToSocket(data);
                    InternetTools.delayBeforeSending(data, listener);

                }else if(data.dst.equals(Locations.SERVER.toString())){

                    //means this packet is for the server
                    System.out.println("Server2Internet - Sending Data to Server");
                    //sender.writeToSocket(data);
                    InternetTools.delayBeforeSending(data, sender);

                }
            }

        }
    }
}
