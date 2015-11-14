package networkemulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by bensoer on 12/11/15.
 */
public final class ConfigurationManager {

    public int clientConnectionPort;
    public String clientConnectionHost;

    public int clientConnectionWindowSize;
    public int clientConnectionInitTimeout;

    public int internetConnectionListenerPort;
    public int internetConnectionSenderPort;
    public String internetConnectionSenderHost;

    public int serverConnectionPort;
    public int serverConnectionWindowSize;
    public int serverConnectionInitTimeout;

    public int clientPacketMaxSize;
    public int serverPacketMaxSize;

    private static ConfigurationManager instance = null;

    public static ConfigurationManager getInstance(){
        if(instance == null){
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private ConfigurationManager(){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("./src/networkemulator/configuration.properties");
            // load a properties file
            prop.load(input);

            this.clientConnectionPort = Integer.parseInt(prop.getProperty("client.connection.port"));
            this.clientConnectionHost = prop.getProperty("client.connection.host");

            this.clientConnectionWindowSize = Integer.parseInt(prop.getProperty("client.connection.windowsize"));
            this.clientConnectionInitTimeout = Integer.parseInt(prop.getProperty("client.connection.inittimeout"));

            this.internetConnectionListenerPort = Integer.parseInt(prop.getProperty("internet.connection.listener.port"));
            this.internetConnectionSenderPort = Integer.parseInt(prop.getProperty("internet.connection.sender.port"));
            this.internetConnectionSenderHost = prop.getProperty("internet.connection.sender.host");

            this.serverConnectionPort = Integer.parseInt(prop.getProperty("server.connection.port"));
            this.serverConnectionWindowSize = Integer.parseInt(prop.getProperty("server.connection.windowsize"));
            this.serverConnectionInitTimeout = Integer.parseInt(prop.getProperty("server.connection.inittimeout"));

            this.clientPacketMaxSize = Integer.parseInt(prop.getProperty("client.packet.maxsize"));
            this.serverPacketMaxSize = Integer.parseInt(prop.getProperty("server.packet.maxsize"));



        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
