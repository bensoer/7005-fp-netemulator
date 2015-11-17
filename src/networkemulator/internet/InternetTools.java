package networkemulator.internet;

import networkemulator.Logger;
import networkemulator.Packet;
import networkemulator.TCPEngine;
import networkemulator.TimerManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bensoer on 10/11/15.
 */
public class InternetTools {

    public static int delayAverage;

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
