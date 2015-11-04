package seana1.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private PrintWriter out;
    private BufferedReader in;

    public TCPEngine(){

    }

    public void createClientSocket(String hostName, int portNumber) throws UnknownHostException, IOException{
        try{
            clientSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        }catch(UnknownHostException uhe){
            System.out.println("Failed to Create Socket");
            uhe.printStackTrace();
            throw uhe;
        }catch(IOException ioe){
            System.out.println("Failed to Access IO");
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public void createServerSocket(int portNumber) throws IOException{
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

    public void startSession() throws NullPointerException, IOException{
        if(serverSocket == null){
            throw new NullPointerException("Server Socket Must Be Initialized before starting a session");
        }

        try{
            serverSessionSocket = serverSocket.accept();
            out = new PrintWriter(serverSessionSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(serverSessionSocket.getInputStream()));
        }catch(IOException ioe){
            System.out.println("Server Could Not Start Session, Unable to Allocate");
            ioe.printStackTrace();
            throw ioe;
        }




    }

    public void writeToSocket(String message){
        out.print(message);
        out.flush();
    }



    public void closeSocket(){
        if(serverSessionSocket != null && !serverSessionSocket.isClosed()){
            try{
                serverSessionSocket.close();
            }catch(IOException ioe){
                System.out.println("Failed to Close the Server Session Socket");
            }

        }

        if(clientSocket != null && !clientSocket.isClosed()){
            try{
                clientSocket.close();
            }catch(IOException ioe){
                System.out.println("Failed to Close the Client Socket");
            }
        }
    }

    public String readFromSocket() throws IOException{
        try{
            String fullMessage = "";
            String userInput;
            while ((userInput = in.readLine()) != null) {
                fullMessage += userInput;

            }

            return fullMessage;
        }catch(IOException ioe){
            System.out.println("Error Reading Content");
            ioe.printStackTrace();
            throw ioe;
        }

    }


}
