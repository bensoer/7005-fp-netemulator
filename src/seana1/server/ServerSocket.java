package seana1.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * This is a very specific wrapper class for the Socket object for this specific scenario.
 * This class allows for the basic upload and download of files with the complimenting ClientSocket
 * @author Sean Hodgkinson
 */
public class ServerSocket {

    private int PORT;       //The Port of the socket
    private Socket SOCKET;  //The socket this class wraps around

    public ServerSocket(int port, Socket socket) {
        this.PORT = port;
        this.SOCKET = socket;

        System.out.println("Connection established with: " + this.SOCKET.getInetAddress().toString() + "\tOver Port: " +
                PORT);
    }

    /**
     * Main listen function
     * This function is used to accept, interpret and respond to both SEND and GET requests
     */
    public void listen() {
        boolean done = false;
        String temp = null;
        String requestType = null;
        String filePath = null;
        String fileName = null;
        String fileContents = null;
        String getReqResponseStatus;
        String getReqFileContents;
        String getReqFileHash;
        DataInputStream dIn = null;
        DataOutputStream dOut = null;

        try {
            dIn = new DataInputStream(this.SOCKET.getInputStream());    //Create input buffer for socket
            dOut = new DataOutputStream(this.SOCKET.getOutputStream()); //Create output buffer for socket
        } catch (IOException ex) {
            System.out.println("Filed to open socket stream");
        }

        try {
            //Request receive
            while (!done)
            {
                byte messageType = dIn.readByte();

                switch (messageType) {
                    case 1: // Get request type
                        requestType = dIn.readUTF();
                        break;
                    case 2: // Get file path for GET request or fileName for SEND request
                        temp = dIn.readUTF();
                        break;
                    case 3: // Get file contents, only run for SEND request
                        fileContents = dIn.readUTF();
                        break;
                    default:
                        done = true;
                }
            }
            if(requestType.equals("GET")) {
                filePath = temp;
            }
            else {
                fileName = temp;
            }
            System.out.println("Received " + requestType + " request for file " + temp);
        }
        catch (IOException ex){
            System.out.println("Failed to get read data from input stream for GET request");
        }

        try {
            //Request response
            if(requestType.equals("GET"))//Get response
            {
                if(checkIfFileExists("./files/" + filePath) == false)   //File does not exist NAck is sent back
                {
                    getReqResponseStatus = "N";
                    getReqFileContents = "";
                    getReqFileHash = "";
                }
                else    //File is found and sent back to client
                {
                    getReqResponseStatus = "Y";
                    getReqFileContents = getFileContents(filePath);
                    getReqFileHash = "" + (getReqFileContents.hashCode());
                }
                //3 Part message transfer (Acc, File Contents, File Hash)
                dOut.writeByte(20);
                dOut.writeUTF(getReqResponseStatus);
                dOut.writeByte(21);
                dOut.writeUTF(getReqFileContents);
                dOut.writeByte(22);
                dOut.writeUTF(getReqFileHash);
                dOut.writeByte(23);
                dOut.flush();
            }
            else//Send response
            {
                //1 Part response (file received hash)
                dOut.writeByte(20);
                dOut.writeUTF("" + fileContents.hashCode());
                dOut.writeByte(21);
                printFileToFileSystem(fileName, fileContents);
            }
            dIn.close();
        } catch (IOException ex) {
            System.out.println("Failed to write data to output stream for GET request");
        }

    }

    /**
     * Checks if file exists on the file system and is accessible
     * @param filePath
     * @return true is file is available and false if file is not available
     */
    private boolean checkIfFileExists(String filePath) {
        if (!new File(filePath).exists()) {
            System.out.println("Could not find file");
            return false;
        }
        return true;
    }

    /**
     * Closes the socket
     */
    public void close() {
        try {
            this.SOCKET.close();
        } catch (IOException ex) {
            System.out.println("Failed to close socket");
            System.exit(3);
        }
    }

    /**
     * Get all contents of file at passed in path inside the ./files/ folder
     * @param filePath
     * @return String of file contents or 'Error' if failed
     */
    private String getFileContents(String filePath)
    {
        String data;
        try {
            data = new Scanner(new File("./files/" + filePath)).useDelimiter("\\Z").next();
        }
        catch (IOException ex){
            System.out.println("Error or getting file contents");
            return "Error";
        }
        return data;
    }

    /**
     * Prints file contents to file location
     * @param fileName
     * @param fileContents
     */
    private void printFileToFileSystem(String fileName, String fileContents)
    {
        try {
            File file = new File("./files/" + fileName);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(fileContents);
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println("Could not save file to system");
        }
    }
}
