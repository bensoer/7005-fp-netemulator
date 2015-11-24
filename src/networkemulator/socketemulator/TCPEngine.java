package networkemulator.socketemulator;

import networkemulator.Logger;
import networkemulator.socketemulator.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by bensoer on 03/11/15.
 *
 * TCPEngine is a socket wrapper for the network emulator and allows easy management and transfer of the Packet objects
 * into the TCP Stream.
 */
public class TCPEngine {


    private Socket clientSocket;

    private ServerSocket serverSocket;
    private Socket serverSessionSocket;
    //private PrintWriter out;
    private ObjectOutputStream out;
    //private BufferedReader in;
    private ObjectInputStream in;

    private ReentrantLock writeLock = new ReentrantLock(true);

    public TCPEngine(){

    }

    /**
     * createClientSocket is a helper method for creating a socket that acts as a client to wherever it is connecting
     * to
     * @param hostName String - the hostname to connect to
     * @param portNumber int - the port number on the host to connect to
     * @throws UnknownHostException - Thrown if the hostName parameter is invalid or unknown
     * @throws IOException - Thrown when IO Resources are unavailable
     */
    public void createClientSocket(String hostName, int portNumber) throws UnknownHostException, IOException{
        Logger.log("TCPEngine - Attempting to connect to " + hostName + " on port " + portNumber);
        try{
            Logger.log("TCPEngine - Creating Client input and output socket buffers");
            clientSocket = new Socket(hostName, portNumber);
            //out = new PrintWriter(clientSocket.getOutputStream(), true);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            //in = new BufferedReader(
                   // new InputStreamReader(clientSocket.getInputStream()));
        }catch(UnknownHostException uhe){
            Logger.log("TCPEngine - Failed to Create Socket");
            uhe.printStackTrace();
            throw uhe;
        }catch(IOException ioe){
            Logger.log("TCPEngine - Failed to Access IO");
            ioe.printStackTrace();
            throw ioe;
        }
    }

    /**
     * createServerSocket is a helper method that creates a socket as if it were belonging to a server. If this method
     * is called multiple times, the previous socket will be closed before a new one is created
     * @param portNumber int - the port number for the server socket to listen on
     * @throws IOException - Thrown if resources are not available
     */
    public void createServerSocket(int portNumber) throws IOException{
        Logger.log("TCPEngine - Attempting to Create Server Socket on port " + portNumber);
        if(serverSocket != null && !serverSocket.isClosed()){
            closeSocket();
        }

        try{
            serverSocket = new ServerSocket(portNumber);
        }catch(IOException ioe){
            Logger.log("Unable to Create Server Socket. Could not Allocate Resources");
            ioe.printStackTrace();
            throw ioe;
        }
    }

    /**
     * startSession is a helper method to be used with createServerSocket for accepting connections from a client (who
     * may have connected by calling the createClientSocket method). This function will throw an exception if the
     * createServerSocket method has not been called yet
     * @return String - The Addresses of the connecting client
     * @throws NullPointerException - Thrown if the serverSocket hasn't been created yet
     * @throws IOException - Thrown if resources are not available
     */
    public String startSession() throws NullPointerException, IOException{
        Logger.log("TCPEngine - Starting A Session");
        if(serverSocket == null){
            throw new NullPointerException("Server Socket Must Be Initialized before starting a session");
        }

        try{
            Logger.log("TCPEngine - Awaiting A Connection Request");
            serverSessionSocket = serverSocket.accept();
            Logger.log("TCPEngine - Allocating Input and Output Buffers for New Connection Request");
            //out = new PrintWriter(serverSessionSocket.getOutputStream(), true);
            out = new ObjectOutputStream(serverSessionSocket.getOutputStream());
            in = new ObjectInputStream(serverSessionSocket.getInputStream());
            //in = new BufferedReader(
              //      new InputStreamReader(serverSessionSocket.getInputStream()));
            return serverSessionSocket.getInetAddress().toString();
        }catch(IOException ioe){
            Logger.log("Server Could Not Start Session, Unable to Allocate");
            ioe.printStackTrace();
            throw ioe;
        }




    }

    /**
     * writeToSocket allows the client to write a Packet object passed in as a parameter to the socket
     * @param packet Packet - the packet being written to the socket
     */
    public void writeToSocket(Packet packet){
        writeLock.lock();
        try{
            out.writeObject(packet);
            out.flush();
        }catch(IOException ioe){
            Logger.log("Failure Serielizing to Socket");
            ioe.printStackTrace();
        }finally {
            writeLock.unlock();
        }

        //out.print(message + "DoNE!");
        //out.flush();
    }

    /**
     * closeSocket closes the socket. It contains a safe guard and will only close the socket if it is already not closed
     * and thus avoids throwing an exception. This method can be used when both createClientSocket and createServerSocket
     * are called as both are checked and only if they are running are they closed.
     */
    public void closeSocket(){
        if(serverSessionSocket != null && !serverSessionSocket.isClosed()){
            Logger.log("TCPEngine - Closing Server Socket");
            try{
                serverSessionSocket.close();
            }catch(IOException ioe){
                Logger.log("Failed to Close the Server Session Socket");
            }

        }

        if(clientSocket != null && !clientSocket.isClosed()){
            Logger.log("TCPEngine - Closing Client Socket");
            try{
                clientSocket.close();
            }catch(IOException ioe){
                Logger.log("Failed to Close the Client Socket");
            }
        }
    }

    /**
     * readFromSocket is a blocking function that waits for a Packet to read from the socket
     * @return Packet - the packet read from the socket
     */
    public Packet readFromSocket(){
        Object result = null;
        try {
            result = in.readObject();

        }catch(EOFException eofe){
            //apparently this is how you handle EOF problems when streaming in java


        }catch(IOException ioe){
            Logger.log("IOException reading packet from socket");
            ioe.printStackTrace();

        }catch(ClassNotFoundException cnfe){
            Logger.log("ClassNotFoundException reading packet from socket");
            cnfe.printStackTrace();
        }
        return (Packet)result;
    }
}
