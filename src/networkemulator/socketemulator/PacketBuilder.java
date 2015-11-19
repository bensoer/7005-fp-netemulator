package networkemulator.socketemulator;

import networkemulator.*;

import java.security.InvalidParameterException;

/**
 * Created by bensoer on 10/11/15.
 *
 * PacketBuilder is a hybrid helper and factory class that builds and sends packets based on specified paramers or even
 * the packet passed in itself
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


    /**
     * setSourceLocation sets the source location that is used for all packets if the parameter is not supplied. Allows for
     * updating the set values in the constructor
     * @param source Locations - a location that is the source of the packet
     */
    public void setSourceLocation(Locations source){
        this.source = source;
    }

    /**
     * setDestinationLocation sets the destination location that is used for all packets if the parameter is not supplied
     * in the method. Allows for updating the set values in the constructor
     * @param destination Locations - a location that is the destination of the packet
     */
    public void setDestinationLocation(Locations destination){
        this.destination = destination;
    }

    /**
     * createPacket creates a packet based on the passed in type, data, source location and destination location. It uses
     * the last known sequence and acknowledgement number when setting the packet. It will also update the sequence number
     * in the windowmanager for the next time createPacket is called
     * @param type PacketType the type of packet being created
     * @param data String the data to be sent in the packet
     * @param source Locations the location that the packet is coming from
     * @param destinations Locations the destination that the packet is going to
     * @return Packet a created packet, ready to be sent
     */
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

    /**
     * an ovverride of createPacket except uses the default Locations values.
     * @param type PacketType - the type of packet being created
     * @param data String - the data going into the packet
     * @return Packet - a created packet, ready to send
     */
    public Packet createPacket(PacketType type, String data){
        return this.createPacket(type, data ,this.source,this.destination);
    }

    /**
     * createResponsePacket creates an appropriate opposite packet based on the passed in packet. It also generates its
     * sequence number and acknowledgement numbers based on the passed in packet. It does not use nor update the window
     * managers sequence or acknowledgement numbers
     * @param packet Packet - the packet to create a valid response to
     * @param data String - the data going into the valid response packet
     * @return Packet - a valid response packet, ready to send
     * @throws InvalidParameterException - exception is thrown if there is no valid packet that can be made from
     * the passed in packet object
     */
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

    /**
     * sendPacket is a static method that sends a packet out the passed in TCPEngine socket. It also will make sure
     * the packet is able to and is added to the passed in windowManager before sending the packet, thus initiating
     * retransmission functionality. The function should idealy be used in a while loop so that the client will keep calling
     * this function until the packet is able to successfuly send
     * @param packet Packet - the packet being sent
     * @param socket TCPEngine - the socket the packet is being sent over
     * @param window WindowManager - the window that the packet will be placed in to keep track of in case a
     *               retransmission is needed
     * @return boolean - status of whether the packet was able to be successfuly added to the window
     */
    public static boolean sendPacket(Packet packet, TCPEngine socket, WindowManager window){



        //set window size to the current size
        packet.windowSize = window.getWindowSpace() - 1;

        //if this is a PUSH we need to recalc sequence numbers
        //if(packet.packetType == PacketType.PUSH.toInt()){
        if(window.previousSEQ < (packet.seqNum + packet.data.length())){
            window.previousSEQ = packet.seqNum + packet.data.length();
        }
        //}


        //if this is an ACK then we don't want to add it to the window to time. We don't need an ACK for an ACK
        if(packet.packetType == PacketType.ACK.toInt()) {
            Logger.log("PacketBuilder - Sending Packet Seq: " + packet.seqNum + " Ack: " + packet.ackNum + " Type: "
                    + packet.packetType + " Src: [" + packet.src + "] Dst: [" + packet.dst + "] WindowSize: " + packet.windowSize);

            socket.writeToSocket(packet);
            return true;
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
