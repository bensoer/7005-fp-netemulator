package networkemulator.internet;

/**
 * Created by bensoer on 07/11/15.
 */
public class Main {

    public static void main(String[] args){
        Internet internet  = new Internet(50);
        internet.startInternet();
        //internet.beginProcessing();
    }
}
