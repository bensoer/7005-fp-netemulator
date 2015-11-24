package networkemulator.internet;

import networkemulator.socketemulator.Locations;
import networkemulator.Logger;
import networkemulator.socketemulator.Packet;
import networkemulator.socketemulator.TCPEngine;

import java.io.IOException;

/**
 * Created by bensoer on 10/11/15.
 *
 * Client2Internet listens for incoming data from the client. It then calculates the delay time and whether or not
 * to drop the packet before sending it out to the socket to the Server
 */
public class Client2Internet extends Thread {

    private TCPEngine listener;
    private TCPEngine sender;

    private int bitErrorPercent;

    public Client2Internet(TCPEngine listener, TCPEngine sender, int bitErrorPercent){
        this.listener = listener;
        this.sender = sender;
        this.bitErrorPercent = bitErrorPercent;
    }


    /**
     * the default entry for the thread. Creates an infinite loop listening for incoming data, then parsing it, calculating
     * whether the packet will be dropped, if so dropping it, if not then calculating the delay time the packet will
     * wait before being sent to the server. The loop uses the 'src' and 'dst' attributes to determine which socket
     * to pass the packet
     */
    @Override
    public void run(){


        while(true){

            Packet data;
            //means the client is sending data to the server
            Logger.log("Client2Internet - Client is Sending Data. Listening to Client");
            data = listener.readFromSocket();
            Logger.log("Client2Internet - Back from read");

            //System.out.println(data);
            if(data == null){
                Logger.log("Client2Internet - Client has Terminated. Not processing Data. Attempting to Recover");
                this.startSession();
                Logger.log("Client2Internet - Recovery Successful");
                continue;
            }

            Logger.log("Client2Internet - Received Packet Seq: " + data.seqNum + " Ack: " + data.ackNum
                    + " Src: [" + data.src + "] Dst: [" + data.dst + "] Type: " + data.packetType + " WindowSize: "
                    + data.windowSize);

            if(InternetTools.dropPacket(this.bitErrorPercent)){
                Logger.log("Client2Internet - Packet with Seq: " + data.seqNum + " is being dropped");
                continue;
            }else{

                Logger.log("Client2Internet - Sending Packet with Seq: " + data.seqNum);

                if(data.dst.equals(Locations.CLIENT.toString())){


                    //means server is sending to the client
                    Logger.log("Client2Internet - Sending Data to Client");
                    //listener.writeToSocket(data);
                    InternetTools.delayBeforeSending(data, listener);

                }else if(data.dst.equals(Locations.SERVER.toString())){

                    //means this packet is for the server
                    Logger.log("Client2Internet - Sending Data to Server");
                    //sender.writeToSocket(data);
                    InternetTools.delayBeforeSending(data, sender);

                }
            }

        }

    }

    /**
     * startSession is a helper function during the setup of the emulator. startSession accepts an incoming connection
     * from the client so as to establish a connection to start transfering data over it. This method is used whenever
     * the client drops. The Client2Internet module then goes into a recovery mode and restarts the connection. Typically
     * though this breaks the simulation and requires the whole system to be restarted.
     */
    private void startSession(){
        try{
            Logger.log("Internet - Waiting for Connections from Client");
            String clientAddress = this.listener.startSession();
            Logger.log("Internet - Accepted Connection from Client " + clientAddress);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
