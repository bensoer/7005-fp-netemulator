package networkemulator.socketemulator;

import networkemulator.*;

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

    public Packet createPacket(PacketType type, String data, Locations source, Locations destinations){

        Packet packet = new Packet();
        packet.packetType = type.toInt();

        packet.data = data;

        //packet.seqNum = sequenceNumber;
        packet.seqNum = this.windowManager.previousSEQ;
        this.windowManager.previousSEQ += data.length();
        packet.ackNum = this.windowManager.previousACK;

        packet.src = source.toString();
        packet.dst = destinations.toString();

        return packet;

    }

    public Packet createPacket(PacketType type, String data){
        return this.createPacket(type, data ,this.source,this.destination);
    }

    public Packet createResponsePacket(Packet packet, String data) throws InvalidParameterException{

        //create response packet
        Packet response = new Packet();

        //set sequence num as the ack num
        response.seqNum = packet.ackNum;

        response.data = data;
        int dataLength = response.data.length();
        response.ackNum = packet.seqNum + dataLength;

        //set the appropriate response type
        if(packet.packetType == PacketType.PUSH.toInt()) {
            response.packetType = PacketType.ACK.toInt();
        }else if(packet.packetType == PacketType.ACK.toInt()){
            packet.packetType = PacketType.PUSH.toInt();
        }else if(packet.packetType == PacketType.EOT.toInt()){
            //throw new InvalidParameterException("EOT Packet is Invalid For Producing an Appropriate Response Packet");
            response.packetType = PacketType.ACK.toInt();
        }


        //set source and destination as eachother
        response.src = packet.dst;
        response.dst = packet.src;

        return response;
    }

    public static boolean sendPacket(Packet packet, TCPEngine socket, WindowManager window){



        //set window size to the current size
        packet.windowSize = window.getWindowSpace() - 1;


        //if this is an ACK then we don't want to add it to the window to time. We don't need an ACK for an ACK
        if(packet.packetType == PacketType.ACK.toInt()) {
            Logger.log("PacketBuilder - Sending Packet Seq: " + packet.seqNum + " Ack: " + packet.ackNum + " Type: "
                    + packet.packetType + " Src: [" + packet.src + "] Dst: [" + packet.dst + "] WindowSize: " + packet.windowSize);

            socket.writeToSocket(packet);
            return true;
        }

        //if this is a PUSH we need to recalc sequence numbers
        if(packet.packetType == PacketType.PUSH.toInt()){
            window.previousSEQ = packet.seqNum + packet.data.length();
        }


        if(!window.canAddPacket(packet)){
            Logger.log("PacketBuilder - Can't Add Packet " + packet.seqNum + " To Window. Window Is Full");
            return false;
        }else{

            Logger.log("PacketBuilder - Sending Packet Seq: " + packet.seqNum + " Ack: " + packet.ackNum + " Type: "
                    + packet.packetType + " Src: [" + packet.src + "] Dst: [" + packet.dst + "] WindowSize: " + packet.windowSize);

            PacketMeta pm = new PacketMeta(socket, packet, window);
            window.push(pm);

            socket.writeToSocket(packet);
            pm.setTimer();
        }

        return true;

    }





}
