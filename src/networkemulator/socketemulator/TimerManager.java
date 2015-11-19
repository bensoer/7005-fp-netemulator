package networkemulator.socketemulator;

import java.util.Timer;

/**
 * Created by bensoer on 12/11/15.
 *
 * TimeManager is a singleton that is used so as to reduce the number of threads and resources used for timers.
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
