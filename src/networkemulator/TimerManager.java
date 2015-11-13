package networkemulator;

import java.util.Timer;

/**
 * Created by bensoer on 12/11/15.
 */
public class TimerManager {

    private static Timer timer = null;

    public static Timer getInstance(){
        if(timer == null){
            timer = new Timer();
        }
        return timer;
    }

}
