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


        response.data = "";

        return response;
    }

    public static boolean sendPacket(Packet packet, TCPEngine socket, WindowManager window){

        int dataLength = packet.data.length();
        packet.ackNum = packet.seqNum + dataLength;

        //set window size to the current size
        packet.windowSize = window.getWindowSpace() - 1;

        Logger.log("PacketBuilder - Sending Packet Seq: " + packet.seqNum + " Ack: " + packet.ackNum + " Type: "
                + packet.packetType + " Src: [" + packet.src + "] Dst: [" + packet.dst + "] WindowSize: " + packet.windowSize);


        //if this is an ACK then we don't want to add it to the window to time. We don't need an ACK for an ACK
        if(packet.packetType == PacketType.ACK.toInt()) {
            socket.writeToSocket(packet);
            return true;
        }


        if(!window.canAddPacket(packet)){
            Logger.log("PacketBuilder - Can't Add Packet To Window. Window Is Full");
            return false;
        }else{

            PacketMeta pm = new PacketMeta(socket, packet, window);
            window.push(pm);

            socket.writeToSocket(packet);
            pm.setTimer();
        }

        return true;

    }





}
