package networkemulator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bensoer on 10/11/15.
 */
public class PacketMeta {

    private Timer timer = new Timer();

    public Packet packet;

    private TCPEngine socket;

    private WindowManager windowManager;

    public boolean acknowledged = false;

    public PacketMeta(TCPEngine socket, Packet packet, WindowManager windowManager){
        this.socket = socket;
        this.packet = packet;
        this.windowManager = windowManager;
    }


    public void setTimer(int delay){
        timer.schedule(new TimerTask(){

            @Override
            public void run(){

                if(!acknowledged){
                    //we need to resend the packet
                    System.out.println("Packet: " + packet.seqNum + " has failed to be ACK'd in time. Resending");

                    double prevFactor = 0;
                    while(!PacketBuilder.sendPacket(packet, socket, windowManager)){
                        long waitTime = (long) Math.pow(200.0, prevFactor);
                        prevFactor++;
                        try{
                            Thread.sleep(waitTime);
                        }catch(InterruptedException ie){
                            System.out.println("PacketMeta - Thread failed to sleep while trying to Resend a packet");
                            ie.printStackTrace();
                        }

                    };

                }
            }

        }, delay);
    }
}
