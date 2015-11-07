package networkemulator;

import networkemulator.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by bensoer on 03/11/15.
 */
public class TCPEngine {


    private Socket clientSocket;

    private ServerSocket serverSocket;
    private Socket serverSessionSocket;
    //private PrintWriter out;
    private ObjectOutputStream out;
    //private BufferedReader in;
    private ObjectInputStream in;

    public TCPEngine(){

    }

    public void createClientSocket(String hostName, int portNumber) throws UnknownHostException, IOException{
        System.out.println("TCPEngine - Attempting to connect to " + hostName + " on port " + portNumber);
        try{
            System.out.println("TCPEngine - Creating Client input and output socket buffers");
            clientSocket = new Socket(hostName, portNumber);
            //out = new PrintWriter(clientSocket.getOutputStream(), true);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            //in = new BufferedReader(
                   // new InputStreamReader(clientSocket.getInputStream()));
        }catch(UnknownHostException uhe){
            System.out.println("TCPEngine - Failed to Create Socket");
            uhe.printStackTrace();
            throw uhe;
        }catch(IOException ioe){
            System.out.println("TCPEngine - Failed to Access IO");
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public void createServerSocket(int portNumber) throws IOException{
        System.out.println("TCPEngine - Attempting to Create Server Socket on port " + portNumber);
        if(serverSocket != null && !serverSocket.isClosed()){
            closeSocket();
        }

        try{
            serverSocket = new ServerSocket(portNumber);
        }catch(IOException ioe){
            System.out.println("Unable to Create Server Socket. Could not Allocate Resources");
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public String startSession() throws NullPointerException, IOException{
        System.out.println("TCPEngine - Starting A Session");
        if(serverSocket == null){
            throw new NullPointerException("Server Socket Must Be Initialized before starting a session");
        }

        try{
            System.out.println("TCPEngine - Awaiting A Connection Request");
            serverSessionSocket = serverSocket.accept();
            System.out.println("TCPEngine - Allocating Input and Output Buffers for New Connection Request");
            //out = new PrintWriter(serverSessionSocket.getOutputStream(), true);
            out = new ObjectOutputStream(serverSessionSocket.getOutputStream());
            in = new ObjectInputStream(serverSessionSocket.getInputStream());
            //in = new BufferedReader(
              //      new InputStreamReader(serverSessionSocket.getInputStream()));
            return serverSessionSocket.getInetAddress().toString();
        }catch(IOException ioe){
            System.out.println("Server Could Not Start Session, Unable to Allocate");
            ioe.printStackTrace();
            throw ioe;
        }




    }

    public void writeToSocket(Packet packet){
        try{
            out.writeObject(packet);
            out.flush();
        }catch(IOException ioe){
            System.out.println("Failure Serielizing to Socket");
            ioe.printStackTrace();
        }

        //out.print(message + "DoNE!");
        //out.flush();
    }



    public void closeSocket(){
        if(serverSessionSocket != null && !serverSessionSocket.isClosed()){
            System.out.println("TCPEngine - Closing Server Socket");
            try{
                serverSessionSocket.close();
            }catch(IOException ioe){
                System.out.println("Failed to Close the Server Session Socket");
            }

        }

        if(clientSocket != null && !clientSocket.isClosed()){
            System.out.println("TCPEngine - Closing Client Socket");
            try{
                clientSocket.close();
            }catch(IOException ioe){
                System.out.println("Failed to Close the Client Socket");
            }
        }
    }

    public Packet readFromSocket(){
        Object result = null;
        try {
            result = in.readObject();

        }catch(EOFException eofe){
            //apparently this is how you handle EOF problems when streaming in java


        }catch(IOException ioe){
            System.out.println("IOException reading packet from socket");
            ioe.printStackTrace();

        }catch(ClassNotFoundException cnfe){
            System.out.println("ClassNotFoundException reading packet from socket");
            cnfe.printStackTrace();
        }
        return (Packet)result;
    }
}
