package seana1.client;

import seana1.Packet;
import seana1.PacketType;
import seana1.SerielizationHelper;
import seana1.internet.TCPEngine;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * This is a very specific wrapper class for the Socket object for this specific scenario.
 * This class facilitates socket connection to a server and unload/download of files to said server
 * @author Sean Hodgkinson
 */
public class ClientSocket
{
    private InetAddress IP_ADDRESS;
    private Socket SOCKET;

    /**
     * This creates the socket that this class will wrap around and makes sure that it works
     * @param ipAddress
     * @param port
     */
    public ClientSocket(InetAddress ipAddress, int port)
    {
        this.IP_ADDRESS = ipAddress;
        try {
            this.SOCKET = new Socket(IP_ADDRESS, port);
        }
        catch (IOException ex) {
            System.out.println("Could not connect to server socket. Exiting");
            System.exit(2);
        }
    }

    /**
     * This sends a SEND request to the server for a specific file
     * @param filePath
     */
    public void sendSendRequest(String filePath)
    {
        String fileContents;
        boolean done = false;
        String fileHash = "";
        DataInputStream dIn = null;
        DataOutputStream dOut = null;

        fileContents = getFileContents(filePath);

        try {
            dIn = new DataInputStream(this.SOCKET.getInputStream());    //Creating socket input stream
            dOut = new DataOutputStream(this.SOCKET.getOutputStream()); //Creating socket output stream
        } catch (IOException ex) {
            System.out.println("Filed to open socket stream");
        }

        try {
            //3 part SEND request (SEND, file path, file contents)
            dOut.writeByte(1);
            dOut.writeUTF("SEND");
            dOut.writeByte(2);
            dOut.writeUTF(filePath);
            dOut.writeByte(3);
            dOut.writeUTF(fileContents);
            dOut.writeByte(4);
            dOut.flush();
        } catch (IOException ex) {
            System.out.println("Failed to write data to socket output stream SEND request");
        }

        try{//Getting SEND request response of file hash to check for integrity
            while (!done) {
                byte messageType = dIn.readByte();

                switch (messageType) {
                    case 20: // File found 'Y' for yes found, 'N' for no not found
                        fileHash = dIn.readUTF();
                        break;
                    default:
                        done = true;
                }
            }
            if(fileContents.hashCode() == Integer.parseInt(fileHash))//Checking file hash vs response hash
                System.out.println("Successfully transferred file to server");
            else
                System.out.println("File may have filed to transfer to server");

        } catch (IOException ex) {
            System.out.println("Failed to read data from socket input stream SEND request");
        }

    }

    /**
     * This sends GET requests for a specific file in the ./files folder on the server
     * @param serverFilePath
     * @param clientOutputPath
     */
    public void sendGetRequest(String serverFilePath, String clientOutputPath)
    {
        DataInputStream dIn = null;
        DataOutputStream dOut = null;
        boolean done = false;
        String requestStatus = null;
        String fileContents = null;
        String fileHash = null;

        try {
            dIn = new DataInputStream(this.SOCKET.getInputStream());    //Creating socket input stream
            dOut = new DataOutputStream(this.SOCKET.getOutputStream()); //Creating socket output stream
        } catch (IOException ex) {
            System.out.println("Failed to open socket stream");
        }

        //read in file contents
        Packet packet = new Packet();
        packet.packetType = PacketType.PUSH.toInt();
        packet.seqNum = 1;
        packet.ackNum = 2;
        packet.data = "GET " + serverFilePath;

        TCPEngine manager = new TCPEngine();
        try{
            manager.createClientSocket("localhost", 8000);
            manager.writeToSocket(packet);
        }catch(UnknownHostException uhe){

        }catch(IOException ioe){

        }

/*
        try{
            String payload = SerielizationHelper.toString(packet);

            dOut.write(payload.getBytes(), 0, payload.getBytes().length);
            dOut.writeUTF("DoNE!");
            //System.out.println("PAYLOAD");
            System.out.println(payload);
            //dOut.writeUTF(payload + "DoNE!");
            dOut.flush();
        }catch(IOException ioe){
            System.out.println("Serielization Failure");
            ioe.printStackTrace();
        }
*/

        //cut up into appropriate number of packets



/*
        try {//2 Part request (GET, server file path) the nil does nothing, it just needs to be 3 parts
            dOut.writeByte(1);
            dOut.writeUTF("GET");
            dOut.writeByte(2);
            dOut.writeUTF(serverFilePath);
            dOut.writeByte(3);
            dOut.writeUTF("nil");
            dOut.writeByte(4);
            dOut.flush();
        } catch (IOException ex) {
            System.out.println("Failed to write data to socket output stream GET request");
        }

        try{//Getting 3 part server response (request status, file contents, file hash)
            while (!done) {
                byte messageType = dIn.readByte();

                switch (messageType) {
                    case 20:
                        requestStatus = dIn.readUTF();
                        break;
                    case 21:
                        fileContents = dIn.readUTF();
                        break;
                    case 22:
                        fileHash = dIn.readUTF();
                        break;
                    default:
                        done = true;
                }
            }
            if(!requestStatus.equals("Y")){//If the file did not come back successfully due to 404 error on server
                System.out.println("File requested not found");
                System.exit(404);
            }
            if(fileContents.hashCode() == Integer.parseInt(fileHash))//Checks file against hash for integrity check
                System.out.println("File was successfully transferred");
            else
                System.out.println("File may have been corrupted during transfer");

            printFileToFileSystem(clientOutputPath + serverFilePath, fileContents);//Save file to system in ./files folder

        } catch (IOException ex) {
            System.out.println("Failed to read data from socket input stream GET request");
        }*/
    }

    /**
     * Saves file to system at specified path
     * @param fileName
     * @param fileContents
     */
    private void printFileToFileSystem(String fileName, String fileContents)
    {
        try {
            Files.write(Paths.get(fileName), fileContents.getBytes());
        } catch (IOException ex) {
            System.out.println("Could not save file to system");
        }
    }

    /**
     * Gets all file contents from file at specific location
     * @param filePath
     * @return string of file contents
     */
    private String getFileContents(String filePath)
    {
        String data;
        try {
            data = new Scanner(new File("./" + filePath)).useDelimiter("\\Z").next();
        }
        catch (IOException ex){
            System.out.println("Error or getting file contents");
            return "Error";
        }
        return data;
    }
}
