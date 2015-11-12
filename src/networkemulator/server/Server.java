package networkemulator.server;

import networkemulator.Logger;
import networkemulator.TCPEngine;
import networkemulator.WindowManager;

/**
 * Created by bensoer on 03/11/15.
 */
public class Server {

    private static TCPEngine manager;
    private static WindowManager wm;


    public static void main(String[] args){
        manager = new TCPEngine();
        wm = new WindowManager(6, 500);
        Logger.configure(true,true, "./ServerLog.txt");
        try{
            manager.createServerSocket(7000);
            Logger.log("Server - Server Created");
            manager.startSession();
            Logger.log("Server - Connection Accepted");
            //manager.closeSocket();

        }catch(Exception e){
            e.printStackTrace();
        }

        Logger.log("Server - Creating Listener Thread");
        Thread sln = new ServerSocketListener(manager, wm);
        sln.start();

        Logger.log("Server - Listening Thread Created");

        try{
            sln.join();
        }catch(InterruptedException ie){
            Logger.log("Server - Interrupt Exception Joing Thread to Main Thread");
        }

    }
}
