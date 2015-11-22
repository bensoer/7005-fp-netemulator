package networkemulator;

import java.io.*;

/**
 * Created by bensoer on 11/11/15.
 *
 * Logger is a static helper class for organizing where logging information is sent to. It can be configured to write
 * both to the console and to file
 */
public class Logger {


    private static boolean writeToFile;
    private static boolean writeToConsole;
    private static String logFile;
    private static FileWriter fw;

    /**
     * configure sets the logging preferences. Note that because this is a statis class, these preferences will have a
     * global effect on the whole application instance
     * @param writeToFile boolean - whether to write data to file or not
     * @param writeToConsole boolean - whether to write data to console or not
     * @param logFile String - the directory or the log file to write to, if it is enabled. If writeToFile is false, pass
     *                null for this parameter
     */
    public static void configure(boolean writeToFile, boolean writeToConsole, String logFile){
        Logger.writeToFile = writeToFile;
        Logger.writeToConsole = writeToConsole;
        Logger.logFile = logFile;
        if(Logger.logFile != null && Logger.writeToFile){
            try{
                fw = new FileWriter(logFile, true);
            }catch(IOException ioe){
                System.out.println("Logger IOException creating log file");
                ioe.printStackTrace();
            }

        }
    }


    /**
     * helper method to write a passed in message to console. This will only work if writeToConsole was set to true
     * @param message String  - the message to write to console
     */
    public static void writeToConsole(String message){
        if(writeToConsole){
            System.out.println(message);
        }
    }

    /**
     * helper method to write to a passed in message to file. This will only work if writeToFile was set to true
     * @param message String - the message to write to file
     */
    public static void writeToFile(String message){
        try{
            fw.write(message + "\n");
            fw.flush();
        }catch(IOException ioe){
            System.out.println("Logger - IOException Writing To File");
            ioe.printStackTrace();
        }
    }

    /**
     * a helper method to write a message to wherever it has been configured to
     * @param message String - the message to log to all configured/set locations
     */
    public static void log(String message){
        if(writeToConsole){
            writeToConsole(message);
        }
        if(writeToFile){
            writeToFile(message);
        }
    }
}
