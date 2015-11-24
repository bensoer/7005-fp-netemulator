package networkemulator.socketemulator;

import networkemulator.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bensoer on 10/11/15.
 *
 * PacketMeta is a data class that stores instance information about the sent packet so that the WindowManager is able to
 * retransmit the packet on timer runout, or get additional information about the packets status at anytime during its
 * transmission and still in the window
 */
public class PacketMeta {

    /** timer Timer - the timer manager for the packet **/
    private Timer timer = TimerManager.getInstance();

    /** packet Packet - the packet this meta is being stored for **/
    public Packet packet;

    /** socket TCPEngine - the socket the packet was sent over. To be used if the packet needs to be sent again **/
    private TCPEngine socket;

    /** windowManager WindowManager - the window manager that is storing this packet meta. To be used if the packet needs
     * to be sent again. The WindowManager will be able to identify the duplicate packet and overwrite it in its window
     */
    private WindowManager windowManager;

    /** acknowledgemend boolean - the status of whether this packet has been acknowledged or not. Default is false. If
     * this value is true, the timer will cancel and the packet will not be retransmitted **/
    public boolean acknowledged = false;

    /** delay int - the amount of delay the timeout should be set to **/
    private int delay;
    /** sendCount int - the number of times the packet has been sent **/
    private int sendCount = 0;

    public PacketMeta(TCPEngine socket, Packet packet, WindowManager windowManager){
        this.socket = socket;
        this.packet = packet;
        this.windowManager = windowManager;
        this.delay = this.windowManager.getInitialPacketDelay();
    }


    /**
     * getSendCount gets how many times the packet has been sent. This is used to calculate timeout time
     * @return int - the number of times the packet has been sent
     */
    public int getsendCount(){
        return sendCount;
    }

    /**
     * setSendCount sets the send count for the packet
     * @param sendCount int - the number of times the packet has been sent
     */
    public void setsendCount(int sendCount){
        this.sendCount = sendCount;
    }


    /**
     * setTimer initializes the timer to start counting down until the packet should be assumed lost and should
     * be retransmitted. Timer wait time is calculated based on the delay value and the send count
     */
    public void setTimer(){
        timer.schedule(new TimerTask(){

            @Override
            public void run(){

                if(!acknowledged){
                    //we need to resend the packet
                    Logger.log("PacketMeta - Packet " + packet.seqNum + " has failed to be ACK'd in time. Resending");

                    double prevFactor = 0;
                    while(!PacketBuilder.sendPacket(packet, socket, windowManager)){
                        Logger.log("PacketMeta - Window Is Full, Waiting to Resend Packet");
                        long waitTime = (long) Math.pow(200.0, prevFactor);
                        prevFactor++;
                        try{
                            Thread.sleep(waitTime);
                        }catch(InterruptedException ie){
                            Logger.log("PacketMeta - Thread failed to sleep while trying to Resend a packet");
                            ie.printStackTrace();
                        }

                    }

                }else{
                    this.cancel();
                }
            }

        }, (long)Math.pow(this.delay, this.sendCount));
    }
}
