package seana1.internet;

/**
 * Created by bensoer on 03/11/15.
 */
public class InternetSocket {

    private TCPEngine listenerManager;
    private TCPEngine senderManager;

    public InternetSocket(){

        listenerManager = new TCPEngine();
        senderManager = new TCPEngine();

    }

    public TCPEngine createListener(){
        try{
            listenerManager.createServerSocket(7000);
            System.out.print("Server Created");

            /*listenerManager.startSession();
            System.out.println("Connection Accepted");
            System.out.println(listenerManager.readFromSocket());
            System.out.println("Message Recieved");
            listenerManager.closeSocket();*/
        }catch(Exception e){
            e.printStackTrace();
        }

        return listenerManager;
    }

    public TCPEngine createSender(String hostName, int portNumber){
        try{
            senderManager.createClientSocket(hostName, portNumber);

        }catch(Exception e){
            e.printStackTrace();
        }
        return senderManager;
    }
}
