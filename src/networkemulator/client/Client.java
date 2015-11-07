package networkemulator.client;


import networkemulator.Packet;
import networkemulator.PacketType;
import networkemulator.TCPEngine;

/**
 * Created by bensoer on 03/11/15.
 */
public class Client {


    private String message = "";

    private TCPEngine manager;

    public static void main(String[] args){

        TCPEngine manager = new TCPEngine();
        try{
            System.out.println("Client - Creating Socket");
            manager.createClientSocket("localhost", 8000);
            System.out.println("Client - Socket Created");

            Packet packet = new Packet();
            packet.seqNum = 893;
            packet.data = "YES";
            packet.ackNum = 784;
            packet.packetType = PacketType.PUSH.toInt();

            Packet packet2 = new Packet();
            packet2.seqNum = 894;
            packet2.data = "LEODLOE";
            packet2.ackNum = 784;
            packet2.packetType = PacketType.PUSH.toInt();

            Packet packet3 = new Packet();
            packet3.seqNum = 895;
            packet3.data = "WOOOT";
            packet3.ackNum = 784;
            packet3.packetType = PacketType.PUSH.toInt();

            Packet packet4 = new Packet();
            packet4.seqNum = 896;
            packet4.data = "EJWEKLEW";
            packet4.ackNum = 784;
            packet4.packetType = PacketType.PUSH.toInt();



            manager.writeToSocket(packet);
            manager.writeToSocket(packet2);
            manager.writeToSocket(packet3);
            manager.writeToSocket(packet4);
           // manager.writeToSocket("HEELOOO OVER THERE");
            System.out.println("Client - Message Sent");
            manager.closeSocket();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
