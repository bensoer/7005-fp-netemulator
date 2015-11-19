package networkemulator.internet;

/**
 * Created by bensoer on 07/11/15.
 *
 * Main is the main entry point for the internet module and parses out the mandatory parameters needed before the Internet
 * module in the emulator can startup.
 */
public class Main {

    public static void main(String[] args){

        String result = scanForValue("-ber", args);
        String avgDelay = scanForValue("-ad", args);
        if(result == null || avgDelay == null){
            System.out.println("Error. Unable to Start Internet Due To Invalid Parameters");
            System.out.println("Expected Use: Main -ber <ber-percent> -ad <avg-delay-ms>");
            return;
        }else{
            System.out.println("Internet - Found Bit Error Rate of: " + result + "%");
            System.out.println("Internet - Found Average Delay of: " + avgDelay + "ms");

        }


        Internet internet  = new Internet(Integer.parseInt(result), Integer.parseInt(avgDelay));
        internet.startInternet();

    }

    /**
     * scanForValue is a helper method that grabs out the string value next to the parameter passed flag in the args
     * array passed at initialization
     * @param flag String - the flag to search for that is before the value desired
     * @param args String[] - the args array possibly containing the flag and value
     * @return String - the value associated with the passed in flag or null if it does not find the flag
     */
    public static String scanForValue(String flag, String[] args){

        for(int i = 0; i < args.length; i++){
            if(args[i].equals(flag)){
                return args[i+1];
            }
        }

        return null;

    }
}
