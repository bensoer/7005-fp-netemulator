package seana1.internet;

import seana1.Packet;
import seana1.PacketBatch;
import seana1.PacketType;
import seana1.SerielizationHelper;

import java.io.IOException;
import java.util.Random;

/**
 * Created by bensoer on 03/11/15.
 */
public class Internet {


    private final int bitErrorPercent;

    private Random generator;

    private boolean isClientsTurn = true;

    private TCPEngine listener;
    private TCPEngine sender;

    public Internet(int bitErrorPercent){
        this.bitErrorPercent = bitErrorPercent;
        this.generator = new Random();
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

    public void startInternet(){

        InternetSocket is = new InternetSocket();

        System.out.println("Internet - Creating Connection Between Internet and Server");
        //this.sender = is.createSender("localhost", 7000);
        this.sender = new TCPEngine();
        try {
            this.sender.createClientSocket("localhost", 7000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Internet - Creating a Listener for Client Connections on port 8000");
        //this.listener = is.createListener(8000);
        this.listener = new TCPEngine();
        try {
            this.listener.createServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.startSession();

        //should we thread here so that both direcitons can be managed in parallel?

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

                System.out.println("HELLO?");
                System.out.println("Internet - Recieved a Packet: \n\t Seq: " + data.seqNum + " \n\t Ack: "
                        + data.ackNum + " \n\t Type: " + data.packetType);

/*
                Packet pb = null;
                try{
                    Object object = SerielizationHelper.toObject(data);
                    pb = (Packet) object;
                    System.out.println("Internet - Recieved a Packet: \n\t Seq: " + pb.seqNum + " \n\t Ack: "
                            + pb.ackNum + " \n\t Type: " + pb.packetType);
                }catch(Exception e){
                    System.out.println("DeSerielization Failed");
                    e.printStackTrace();
                }
*/

                //based on set error rate decide what to remove from it and if to remove anything from it
                int maxValue = 100 / this.bitErrorPercent;
                int number = generator.nextInt(maxValue);
                //iunno if this even makes sense but it probably will work lol
                System.out.println(number);
                if(number == (maxValue/2)){
                    System.out.println("Internet - Packet with Seq: " + data.seqNum + " is being dropped");
                    continue;
                }else{

                    if(this.isClientsTurn){
                        //means client is sending to the server
                        //sender.writeToSocket(data);
                    }else{
                        //means server is sending to the client
                        //listener.writeToSocket(data);
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
