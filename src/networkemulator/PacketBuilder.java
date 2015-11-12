package networkemulator;

import java.security.InvalidParameterException;

/**
 * Created by bensoer on 10/11/15.
 */
public class PacketBuilder {

    private Locations source;
    private Locations destination;

    private WindowManager windowManager;



    public PacketBuilder(Locations source, Locations destination, WindowManager wm){
        this.source = source;
        this.destination = destination;
        this.windowManager = wm;
    }

    public void setSourceLocation(Locations source){
        this.source = source;
    }

    public void setDestinationLocation(Locations destination){
        this.destination = destination;
    }

    public Packet createPacket(PacketType type, int sequenceNumber, Locations source, Locations destinations){

        Packet packet = new Packet();
        packet.packetType = type.toInt();
        packet.windowSize = windowManager.getWindowSpace();

        packet.seqNum = sequenceNumber;

        packet.src = source.toString();
        packet.dst = destinations.toString();

        packet.data = "";

        return packet;

    }

    public Packet createPacket(PacketType type, int sequenceNumber){
        return this.createPacket(type, sequenceNumber,this.source,this.destination);
    }

    public Packet createResponsePacket(Packet packet) throws InvalidParameterException{

        //create response packet
        Packet response = new Packet();

        //set sequence num as the ack num
        response.seqNum = packet.ackNum;

        //set the appropriate response type
        if(packet.packetType == PacketType.PUSH.toInt()) {
            response.packetType = PacketType.ACK.toInt();
        }else if(packet.packetType == PacketType.ACK.toInt()){
            packet.packetType = PacketType.PUSH.toInt();
        }else if(packet.packetType == PacketType.EOT.toInt()){
            throw new InvalidParameterException("EOT Packet is Invalid For Producing an Appropriate Response Packet");
        }

        //set source and destination as eachother
        response.src = packet.dst;
        response.dst = packet.src;

        //set the window size
        response.windowSize = windowManager.getWindowSpace();

        response.data = "";

        return response;
    }

    public static boolean sendPacket(Packet packet, TCPEngine socket, WindowManager window){

        int dataLength = packet.data.length();
        packet.ackNum = packet.seqNum + dataLength + 1;

        Logger.log("PacketBuilder - Sending Packet Seq: " + packet.seqNum + " Ack: " + packet.ackNum + " Type: "
                + packet.packetType + " Src: [" + packet.src + "] Dst: [" + packet.dst + "] WindowSize: " + packet.windowSize);


        if(!window.canAddPacket()){
            Logger.log("PacketBuilder - Can't Add Packet To Window. Window Is Full");
            return false;
        }else{
            PacketMeta pm = new PacketMeta(socket, packet, window);
            window.push(pm);

            socket.writeToSocket(packet);
            pm.setTimer(5000);
            return true;
        }

    }


    /**
     * this overload of sendPacket is for sending packets without adding them to the window as unacknowledged. This
     * is mainly used for explicit acknowledgement packets which themselves do not need to be acknowledged
     * @param packet Packet  - the packet being sent
     * @param socket TCPEngine - the socket the packet is being sent over
     */
    public static void sendPacket(Packet packet, TCPEngine socket){


        int dataLength = packet.data.length();
        packet.ackNum = packet.seqNum + dataLength + 1;

        Logger.log("PacketBuilder - Sending Packet Seq: " + packet.seqNum + " Ack: " + packet.ackNum + " Type: "
                + packet.packetType + " Src: [" + packet.src + "] Dst: [" + packet.dst + "] WindowSize: " + packet.windowSize);

        socket.writeToSocket(packet);
    }




}
