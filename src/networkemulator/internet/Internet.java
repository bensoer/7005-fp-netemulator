package networkemulator.internet;

import networkemulator.TCPEngine;
import networkemulator.Packet;
import networkemulator.PacketType;

import java.io.IOException;
import java.util.Random;

/**
 * Created by bensoer on 03/11/15.
 */
public class Internet {


    private TCPEngine listener = new TCPEngine();
    private TCPEngine sender = new TCPEngine();

    private boolean isClientsTurn = true;

    private Random generator = new Random();

    private int bitErrorPercent;

    public Internet(int bitErrorPercent){
        this.bitErrorPercent = bitErrorPercent;
    }

    public void startInternet(){

        //setup listener for incoming client connections
        try{
            listener.createServerSocket(8000);
        }catch(IOException ioe){
            System.out.println("Failed to Setup Listener's Resources");
            ioe.printStackTrace();
        }


        //setup sender to connect with the server
        try{
            sender.createClientSocket("localhost", 7000);
        }catch(IOException ioe){
            System.out.println("Failed to Allocated Sender's Recources");
            ioe.printStackTrace();
        }

        //starts the listener to look for incoming client requests
        startSession();
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

    public void beginProcessing(){


        while(true){

            Packet data;
            //collect serielized object and make instance of it
            if(this.isClientsTurn){
                //means the client is sending data to the server
                System.out.println("Internet - Client is Sending Data. Listening to Client");
                data = listener.readFromSocket();
                System.out.println("Back from read");
                //System.out.println(data);
                if(data == null){
                    System.out.println("Internet - Client has Terminated. Not processing Data. Attempting to Recover");
                    this.startSession();
                    System.out.println("Internet - Recovery Successful");
                    continue;
                }
            }else{
                System.out.println("Internet - Server is Sending Data. Listening to Server");
                data = sender.readFromSocket();
                //System.out.println(data);
                if(data == null){
                    System.out.println("Internet - Server has Terminated. Not processing Data. Unable To Recover. Aborting");
                    break;
                }
            }

            System.out.println("Internet - Recieved a Packet: \n\t Seq: " + data.seqNum + " \n\t Ack: "
                    + data.ackNum + " \n\t Type: " + data.packetType);


            //based on set error rate decide what to remove from it and if to remove anything from it
            int maxValue = 100 / this.bitErrorPercent;
            int number = generator.nextInt(maxValue);
            //iunno if this even makes sense but it probably will work lol
            System.out.println(number);
            if(number == (maxValue/2)){
                System.out.println("Internet - Packet with Seq: " + data.seqNum + " is being dropped");
                continue;
            }else{

                System.out.println("Internet - Sending Data");
                if(this.isClientsTurn){
                    //means client is sending to the server
                    System.out.println("Internet - Sending Data to Server");
                    sender.writeToSocket(data);
                }else{
                    //means server is sending to the client
                    System.out.println("Internet - Sending Data to Client");
                    listener.writeToSocket(data);
                }

                //if this packet is end of transmission and it has succeeded and will be sent, we need to switch
                //where to expect data coming from
                if(data.packetType == PacketType.EOT.toInt()){
                    this.isClientsTurn = (!this.isClientsTurn);
                }

            }




        }

    }




}
