package networkemulator.client;


import networkemulator.*;

/**
 * Created by bensoer on 03/11/15.
 */
public class Client {


    public static void main(String[] args){

        Logger.configure(true,true, "./ClientLog.txt");
        WindowManager wm = new WindowManager(6, 500);
        PacketBuilder pb = new PacketBuilder(Locations.CLIENT, Locations.SERVER, wm);

        TCPEngine manager = new TCPEngine();
        try{
            Logger.log("Client - Creating Socket");
            manager.createClientSocket("localhost", 8000);
            Logger.log("Client - Socket Created");

        }catch(Exception e){
            e.printStackTrace();
        }
            Logger.log("Client - Creating Listener Thread");
            Thread cln = new ClientSocketListener(manager, wm);
            cln.start();

            Logger.log("Client - Listening Thread Created");


            Packet packet = pb.createPacket(PacketType.PUSH,0);
            packet.data = "HELLO WORLD";

        try{
            Logger.log("Client - Sending Packet");
            while(!PacketBuilder.sendPacket(packet, manager, wm)){
                Logger.log("Client - Couldn't Send - Sleeping and Trying Again");
                Thread.sleep(200);
            }
        }catch(InterruptedException ie){
            Logger.log("Interrupt Exception Sending Packet From Client");
            ie.printStackTrace();
        }



            //System.out.println("Client - Message Sent");
            //manager.closeSocket();

        try{
            cln.join();
        }catch(InterruptedException ie){
            Logger.log("Client - Interrupt Exception Joing Thread to Main Thread");
        }

    }


}
