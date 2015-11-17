package networkemulator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bensoer on 10/11/15.
 */
public class PacketMeta {

    private Timer timer = TimerManager.getInstance();

    public Packet packet;

    private TCPEngine socket;

    private WindowManager windowManager;

    public boolean acknowledged = false;

    private int delay;
    private int sendCount = 0;

    public PacketMeta(TCPEngine socket, Packet packet, WindowManager windowManager){
        this.socket = socket;
        this.packet = packet;
        this.windowManager = windowManager;
        this.delay = this.windowManager.getInitialPacketDelay();
    }



    public int getsendCount(){
        return sendCount;
    }

    public void setsendCount(int sendCount){
        this.sendCount = sendCount;
    }


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
                            System.out.println("PacketMeta - Thread failed to sleep while trying to Resend a packet");
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
