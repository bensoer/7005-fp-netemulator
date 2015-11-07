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

    public Internet(int bitErrorPercent){
        this.bitErrorPercent = bitErrorPercent;
        this.generator = new Random();
    }


    public void startInternet(){

        InternetSocket is = new InternetSocket();

        System.out.println("Internet - Creating Connection Between Internet and Server");
        TCPEngine sender = is.createSender("localhost", 7000);

        System.out.println("Internet - Creating a Listener for Client Connections on port 8000");
        TCPEngine listener = is.createListener(8000);

        try{
            System.out.println("Internet - Waiting for Connections from Client");
            String clientAddress = listener.startSession();
            System.out.println("Internet - Accepted Connection from Client " + clientAddress);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }


        //should we thread here so that both direcitons can be managed in parallel?

        while(true){

            try{

                String data;
                //collect serielized object and make instance of it
                if(this.isClientsTurn){
                    //means the client is sending data to the server
                    data = listener.readFromSocket();
                }else{
                    data = sender.readFromSocket();
                }


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

                //based on set error rate decide what to remove from it and if to remove anything from it
                int maxValue = 100 / this.bitErrorPercent;
                int number = generator.nextInt(maxValue);
                //iunno if this even makes sense but it probably will work lol
                System.out.println(number);
                if(number == (maxValue/2)){
                    System.out.println("Internet - Packet with Seq: " + pb.seqNum + " is being dropped");
                    continue;
                }else{

                    if(this.isClientsTurn){
                        //means client is sending to the server
                        sender.writeToSocket(data);
                    }else{
                        //means server is sending to the client
                        listener.writeToSocket(data);
                    }

                    //if this packet is end of transmission and it has succeeded and will be sent, we need to switch
                    //where to expect data coming from
                    if(pb.packetType == PacketType.EOT.toInt()){
                        this.isClientsTurn = (!this.isClientsTurn);
                    }

                }


            }catch(IOException io){
                System.out.println("IOException Error");
                io.printStackTrace();
            }

        }
    }
}
