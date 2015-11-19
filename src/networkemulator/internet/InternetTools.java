package networkemulator.internet;

import networkemulator.Logger;
import networkemulator.socketemulator.Packet;
import networkemulator.socketemulator.TCPEngine;
import networkemulator.socketemulator.TimerManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bensoer on 10/11/15.
 *
 * InternetTools is a static helper class for the internet module for calculating whether to drop a packet and calculate
 * the amount of delay to give based on passed in BER and Avg. Delay values
 */
public class InternetTools {

    /** the average delay to be used in calculating how much to delay a packet **/
    public static int delayAverage;

    /**
     * dropPacket determines whether or not to drop a packet. Using a random number generator it calculates a number
     * between 0 and 100 and if the value is less then the bit error percent (a number representing 0 - 100% BER) it
     * returns true
     * @param bitErrorPercent int - the Bit Error Percent as a number from 0 to 100. 0 meaning 0% BER 100 meaning 100% BER
     * @return boolean - State of whether to drop or not drop the packet
     */
    public static boolean dropPacket(int bitErrorPercent){
        Random generator = new Random();
        int number = generator.nextInt(100);

        if(number < bitErrorPercent){
            return true;
        }else{
            return false;
        }

       /* if(bitErrorPercent == 0){
            return false;
        }

        int maxValue = 100 / bitErrorPercent;
        int number = generator.nextInt(maxValue);

        if(number == (maxValue/2)){
            return true;
        }else{
            return false;
        }*/
    }

    /**
     * delayBeforeSending sets the delay length timer before sending the packet over the network
     * @param packet Packet - the packet to be sent after its delay time
     * @param manager TCPEngine - the socket to send the packet over.
     */
    public static void delayBeforeSending(Packet packet, TCPEngine manager){

        Random generator = new Random();
        int variance = generator.nextInt(10);
        boolean posotive = generator.nextBoolean();

        int actualDelay = 0;
        if(posotive){
            actualDelay = delayAverage + variance;
        }else{
            actualDelay = delayAverage - variance;
        }

        Logger.log("Internet Tools - Delay Time Set. It is: " + actualDelay + "ms");
        Timer timer = TimerManager.getInstance();

        timer.schedule(new TimerTask(){

            @Override
            public void run(){
                manager.writeToSocket(packet);
            }
        }, actualDelay);

    }
}
