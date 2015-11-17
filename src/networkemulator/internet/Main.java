package networkemulator.internet;

/**
 * Created by bensoer on 07/11/15.
 */
public class Main {

    public static void main(String[] args){

        String result = scanForValue("-ber", args);
        if(result == null){
            System.out.println("Error. Unable to Start Internet Due To Invalid Parameters");
            System.out.println("Expected Use: Main -ber <ber-percent>");
            return;
        }else{
            System.out.println("Internet - Found Bit Error Rate of: " + result + "%");
            Internet internet  = new Internet(Integer.parseInt(result));
            internet.startInternet();
        }

    }

    public static String scanForValue(String flag, String[] args){

        for(int i = 0; i < args.length; i++){
            if(args[i].equals(flag)){
                return args[i+1];
            }
        }

        return null;

    }
}
