package networkemulator.server;

import networkemulator.Packet;
import networkemulator.TCPEngine;

/**
 * Created by bensoer on 03/11/15.
 */
public class Server {

    private static TCPEngine manager;


    public static void main(String[] args){
        manager = new TCPEngine();
        try{
            manager.createServerSocket(7000);
            System.out.println("Server - Server Created");
            manager.startSession();
            System.out.println("Server - Connection Accepted");

            while(true){
                Packet data = manager.readFromSocket();
                System.out.println("Server - Message Recieved");
                System.out.println(data.data);
            }
            //manager.closeSocket();

        }catch(Exception e){
            e.printStackTrace();
        }




    }
}
