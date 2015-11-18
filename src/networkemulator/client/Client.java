package networkemulator.client;


import networkemulator.*;
import networkemulator.socketemulator.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by bensoer on 03/11/15.
 */
public class Client {


    public static void main(String[] args){

        ConfigurationManager cm = ConfigurationManager.getInstance();

        Logger.configure(true,true, "./ClientLog.txt");
        WindowManager wm = new WindowManager(cm.clientConnectionWindowSize, cm.clientConnectionInitTimeout);
        PacketBuilder pb = new PacketBuilder(Locations.CLIENT, Locations.SERVER, wm);

        TCPEngine manager = new TCPEngine();
        try{
            Logger.log("Client - Creating Socket");
            manager.createClientSocket("localhost", cm.clientConnectionPort);
            Logger.log("Client - Socket Created");

        }catch(Exception e){
            e.printStackTrace();
        }

        Logger.log("Client - Creating Listener Thread");
        Thread cln = new ClientSocketListener(manager, wm);
        cln.start();
        Logger.log("Client - Listening Thread Created");



        ArrayList<Packet> delivery = loadFileIntoPackets(pb);

        for(Packet packet : delivery){

            try{
                Logger.log("Client - Sending Packet");
                while(!PacketBuilder.sendPacket(packet, manager, wm)){
                    Logger.log("Client - Couldn't Send - Sleeping and Trying Again");
                    Thread.sleep(600);
                }
            }catch(InterruptedException ie){
                Logger.log("Interrupt Exception Sending Packet From Client");
                ie.printStackTrace();
            }
        }

        try{
            cln.join();
        }catch(InterruptedException ie){
            Logger.log("Client - Interrupt Exception Joing Thread to Main Thread");
        }


            //System.out.println("Client - Message Sent");
            //manager.closeSocket();



    }

    private static ArrayList<Packet> loadFileIntoPackets(PacketBuilder pb){
        ConfigurationManager cm = ConfigurationManager.getInstance();
        try{
            ArrayList<Packet> list = new ArrayList<Packet>();
            File file = new File("./files/client/300loriumipsum.txt");
            byte[] data = Files.readAllBytes(file.toPath());
            String strData = new String(data, "UTF-8");
            //System.out.println(strData);
            for(int i = 0; i < strData.length(); i += cm.clientPacketMaxSize){


                int endIndex = i + cm.clientPacketMaxSize;
                if(endIndex >= strData.length()){
                    endIndex = strData.length();
                }

                String subData = strData.substring(i, endIndex);
                Packet packet = pb.createPacket(PacketType.PUSH,subData);
                list.add(packet);
            }


            Packet eot = pb.createPacket(PacketType.EOT, "");
            list.add(eot);

            return list;
        }catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


}
