package networkemulator.internet;

import networkemulator.Locations;
import networkemulator.Logger;
import networkemulator.Packet;
import networkemulator.TCPEngine;

import java.io.IOException;

/**
 * Created by bensoer on 10/11/15.
 */
public class Client2Internet extends Thread {

    private TCPEngine listener;
    private TCPEngine sender;

    public Client2Internet(TCPEngine listener, TCPEngine sender){
        this.listener = listener;
        this.sender = sender;
    }


    @Override
    public void run(){


        while(true){

            Packet data;
            //means the client is sending data to the server
            System.out.println("Client2Internet - Client is Sending Data. Listening to Client");
            data = listener.readFromSocket();
            System.out.println("Client2Internet - Back from read");
            Logger.log("Client2Internet - Received Packet Seq: " + data.seqNum + " Ack: " + data.ackNum
                    + " Src: [" + data.src + "] Dst: [" + data.dst + "] Type: " + data.packetType + " WindowSize: "
                    + data.windowSize);
            //System.out.println(data);
            if(data == null){
                System.out.println("Client2Internet - Client has Terminated. Not processing Data. Attempting to Recover");
                this.startSession();
                System.out.println("Client2Internet - Recovery Successful");
                continue;
            }

            if(InternetTools.dropPacket(50)){
                System.out.println("Client2Internet - Packet with Seq: " + data.seqNum + " is being dropped");
                continue;
            }else{

                System.out.println("Client2Internet - Sending Packet with Seq: " + data.seqNum);

                if(data.dst.equals(Locations.CLIENT.toString())){

                    //means server is sending to the client
                    System.out.println("Client2Internet - Sending Data to Client");
                    listener.writeToSocket(data);

                }else if(data.dst.equals(Locations.SERVER.toString())){

                    //means this packet is for the server
                    System.out.println("Client2Internet - Sending Data to Server");
                    sender.writeToSocket(data);

                }
            }

        }

    }

    private void startSession(){
        try{
            System.out.println("Internet - Waiting for Connections from Client");
            String clientAddress = this.listener.startSession();
            System.out.println("Internet - Accepted Connection from Client " + clientAddress);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
