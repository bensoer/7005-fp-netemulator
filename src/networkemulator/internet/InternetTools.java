package networkemulator.internet;

import java.util.Random;

/**
 * Created by bensoer on 10/11/15.
 */
public class InternetTools {

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
}
