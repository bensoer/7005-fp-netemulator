package networkemulator.server;

import seana1.internet.TCPEngine;

/**
 * Created by bensoer on 03/11/15.
 */
public class Server {

    private static TCPEngine manager;


    public static void main(String[] args){
        manager = new TCPEngine();
        try{
            manager.createServerSocket(7000);
            System.out.print("Server Created");
            manager.startSession();
            System.out.println("Connection Accepted");
            System.out.println(manager.readFromSocket());
            System.out.println("Message Recieved");
            manager.closeSocket();
        }catch(Exception e){
            e.printStackTrace();
        }




    }
}
