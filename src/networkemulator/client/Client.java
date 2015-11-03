package networkemulator.client;

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
            manager.createClientSocket("localhost", 7000);
            System.out.println("Socket Created");
            manager.writeToSocket("HEELOOO OVER THERE");
            System.out.println("Message Sent");
            manager.closeSocket();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
