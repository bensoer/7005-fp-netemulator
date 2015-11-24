package networkemulator.server;

import networkemulator.*;
import networkemulator.socketemulator.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by bensoer on 16/11/15.
 *
 * The ServerSocketSender is a seperate thread that sends data back to the client once it is initialized. The class is
 * called upon by the ServerSocketListener when it detects the EOT packet has been recieved and all packets have been
 * recieved
 */
public class ServerSocketSender extends Thread {


    private TCPEngine manager;
    private WindowManager wm;

    public ServerSocketSender(TCPEngine manager, WindowManager wm){
        this.manager = manager;
        this.wm = wm;

    }

    /**
     * the main entrance for Thread. Calls sendDataBack()
     */
    @Override
    public void run(){
        sendDataBack();
    }

    /**
     * sendDataBack is a helper method that sends data back to the client. The method terminates when it has been able
     * to pass all of its packets to the PacketBuilder's sendPacket method
     */
    private void sendDataBack(){

        ArrayList<Packet> delivery = loadFileIntoPackets();

        for(Packet packet : delivery){

            try{
                Logger.log("Server - Sending Packet");
                while(!PacketBuilder.sendPacket(packet, manager, wm)){
                    Logger.log("Server - Couldn't Send - Sleeping and Trying Again");
                    Thread.sleep(600);
                }
            }catch(InterruptedException ie){
                Logger.log("Interrupt Exception Sending Packet From Server");
                ie.printStackTrace();
            }
        }
    }

    /**
     * loadFileIntoPackets is a helper method that reads in the hardcoded file and parses it into packets based on the
     * configured max packet size. It then loads these packets into an array list which it returns
     * @return ArrayList<Packet> - The list of packets to be sent. Includes the EOT packet
     */
    private ArrayList<Packet> loadFileIntoPackets(){
        PacketBuilder pb = new PacketBuilder(Locations.SERVER, Locations.CLIENT, wm);
        ConfigurationManager cm = ConfigurationManager.getInstance();
        try{
            ArrayList<Packet> list = new ArrayList<Packet>();
            File file = new File("./files/server/serveripsum.txt");
            byte[] data = Files.readAllBytes(file.toPath());
            String strData = new String(data, "UTF-8");
            //System.out.println(strData);
            for(int i = 0; i < strData.length(); i += cm.serverPacketMaxSize){


                int endIndex = i + cm.serverPacketMaxSize;
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
