package seana1.server;

import java.io.IOException;
import java.net.Socket;

/**
 * This class controls the ServerSocket
 * This is where the Server socket accept() method is called so the server may loop forever
 * @author Sean Hodgkinson
 */
public class Server
{
    private int PORT;
    private java.net.ServerSocket SOCKET;

    /**
     * Creates server object that manages the ServerSocket
     * @param args
     */
    public Server(String[] args)
    {
        if(args.length == 1)
        {
            PORT = Integer.parseInt(args[0]);
        }
        else
        {
            System.out.println("Incorrect usage: java -jar Port");
            System.exit(1);
        }
    }

    /**
     * This starts the server, accepts connections, servers requests then listens again for new connections
     * @Warning loops forever as of now
     */
    public void startServer()
    {
        boolean serverRunning = true;
        Socket connectionSocket;
        System.out.println("Attempting to start server on port " + PORT + "");

            try {   //Initializing the socket
                this.SOCKET = new java.net.ServerSocket(PORT);
                System.out.println("Server started on port " + PORT + " awaiting connection.");
            } catch (IOException ex) {
                System.out.println("Could not create server socket, port may already be in use. Exiting");
                System.exit(2);
            }


        while (serverRunning) {
            try {
                connectionSocket = SOCKET.accept();
                ServerSocket serverSocket = new ServerSocket(PORT, connectionSocket);
                serverSocket.listen();
                serverSocket.close();
            } catch (IOException ex) {
                System.out.println("Could not connect to client, aborting");
                System.exit(2);
            }
        }
        try {
            this.SOCKET.close();
        } catch (IOException e) {
            System.out.println("Could not close server socket");
        }
    }
}
