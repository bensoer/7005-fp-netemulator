package networkemulator.client;


import networkemulator.*;

/**
 * Created by bensoer on 03/11/15.
 */
public class Client {


    public static void main(String[] args){

        WindowManager wm = new WindowManager(6);
        PacketBuilder pb = new PacketBuilder(Locations.CLIENT, Locations.SERVER, wm);

        TCPEngine manager = new TCPEngine();
        try{
            System.out.println("Client - Creating Socket");
            manager.createClientSocket("localhost", 8000);
            System.out.println("Client - Socket Created");

        }catch(Exception e){
            e.printStackTrace();
        }
            System.out.println("Client - Creating Listener Thread");
            Thread cln = new ClientSocketListener(manager, wm);
            cln.start();

            System.out.println("Client - Listening Thread Created");


            Packet packet = pb.createPacket(PacketType.PUSH,0);
            packet.data = "HELLO WORLD";

        try{
            System.out.println("Client - Sending Packet");
            while(!PacketBuilder.sendPacket(packet, manager, wm)){
                System.out.println("Client - Couldn't Send - Sleeping and Trying Again");
                Thread.sleep(200);
            }
        }catch(InterruptedException ie){
            System.out.println("Interrupt Exception Sending Packet From Client");
            ie.printStackTrace();
        }



            //System.out.println("Client - Message Sent");
            //manager.closeSocket();

        try{
            cln.join();
        }catch(InterruptedException ie){
            System.out.println("Client - Interrupt Exception Joing Thread to Main Thread");
        }

    }


}
