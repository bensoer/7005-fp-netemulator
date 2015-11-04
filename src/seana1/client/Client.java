package seana1.client;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * This is the control class for the Client Socket object
 * @author Sean Hodgkinson
 */
public class Client {
    private String FILE_PATH;
    private String FILE_NAME;
    private String REQUEST_TYPE;
    private InetAddress IP_ADDRESS;
    private int PORT;

    /**
     * Constructor for Client object, makes sure that the client is set up properly or quits
     * @param args
     */
    public Client(String[] args) {
        Boolean status;



        if (args.length == 4)//FilePath RequestType IP Port
        {
            String filePath = args[0];
            String requestType = args[1];
            String host = args[2];
            String port = args[3];
            status = setArgs(filePath, requestType, host, port);
        } else {
            status = false;
        }

        if (status == false) {
            System.out.println("Incorrect usage: java -jar ./Client.jar ./File_Path (\"GET\", \"SEND\") Host Port");
            System.exit(1);
        }
    }

    /**
     *  Sets up the GET or SEND request and calls the proper method of the ClientSocket class
     */
    public void startClient()
    {
        ClientSocket clientSocket = new ClientSocket(IP_ADDRESS, PORT);

        if(REQUEST_TYPE.equals("GET")) {
            clientSocket.sendGetRequest(this.FILE_PATH, "./files/");
        } else if(REQUEST_TYPE.equals("SEND")){
            clientSocket.sendSendRequest(this.FILE_PATH);
        } else {
            System.out.println("Error on request method");
            System.exit(1);
        }

    }

    /**
     * Parses inputted args and sets them at the object level, this is also where any arg validations should go
     *
     * @param filePath
     * @param requestType
     * @param host
     * @param port
     * @return True if valid args and false if the args are invalid
     */
    private final boolean setArgs(String filePath, String requestType, String host, String port) {
        if(requestType.equals("SEND"))
            if (checkIfFileExists(filePath) == false)
                return false;
        this.FILE_PATH = filePath;
        this.REQUEST_TYPE = requestType.toUpperCase();
        if(requestType.equals("SEND"))
            this.FILE_NAME = getFileNameFromPath(filePath);
        else
            this.FILE_NAME = null;
        if (checkIfRequestTypeIsValid(requestType) == false) {
            return false;
        }
        this.IP_ADDRESS = getIpFromHost(host);
        this.PORT = Integer.parseInt(port);

        return true;
    }

    /**
     * Resolves host name and returns corresponding ip address of host
     *
     * @param host
     * @return String ip address from inputted host name
     */
    private final InetAddress getIpFromHost(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            System.out.println("Could not resolve host");
        }
        return null;
    }

    /**
     * Gets the name of the file from the inputted path to the file
     *
     * @param filePath
     * @return Name of file, should never return null
     */
    private final String getFileNameFromPath(String filePath) {
        try {
            return Paths.get(filePath).getFileName().toString();
        } catch (InvalidPathException ex) {
            System.out.println("Could not find file");
            System.exit(404);
        }
        return null;
    }

    /**
     * Checks if request type is valid
     *
     * @param requestType
     * @return True if request type is valid and false if request type is invalid
     */
    private final boolean checkIfRequestTypeIsValid(String requestType) {
        return (requestType.equalsIgnoreCase("GET") || requestType.equalsIgnoreCase("SEND"));
    }

    /**
     * Checks if file at path exists and sets FILEPATH if it does exist
     *
     * @param filePath
     * @return True if file exists and false if file does not exist of cannot be accessed
     */
    private boolean checkIfFileExists(String filePath) {
        if (!new File(filePath).exists()) {
            System.out.println("Could not find file");
            System.exit(404);
        }
        return true;
    }
}
