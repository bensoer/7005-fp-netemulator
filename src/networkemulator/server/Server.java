package networkemulator.server;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import networkemulator.ConfigurationManager;
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
        ConfigurationManager cm = ConfigurationManager.getInstance();
        wm = new WindowManager(cm.serverConnectionWindowSize, cm.serverConnectionInitTimeout);
        Logger.configure(true,true, "./ServerLog.txt");
        try{
            manager.createServerSocket(cm.serverConnectionPort);
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
