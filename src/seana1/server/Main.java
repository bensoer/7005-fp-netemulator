package seana1.server;

import java.lang.reflect.Array;

/**
 * Main driver class for Server side application for COMP 7005 Assignment 1
 * @author Sean Hodgkinson
 */
public class Main
{
    public static void main(String[] args)
    {
        //temporary hack
//        args[0] = "7000";
        Server server = new Server(args);
        server.startServer();
    }
}
