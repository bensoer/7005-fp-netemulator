package networkemulator;

import java.io.*;

/**
 * Created by bensoer on 11/11/15.
 */
public class Logger {


    private static boolean writeToFile;
    private static boolean writeToConsole;
    private static String logFile;
    private static FileWriter fw;

    public static void configure(boolean writeToFile, boolean writeToConsole, String logFile){
        Logger.writeToFile = writeToFile;
        Logger.writeToConsole = writeToConsole;
        Logger.logFile = logFile;
        if(Logger.logFile != null){
            try{
                fw = new FileWriter(logFile, true);
            }catch(IOException ioe){
                System.out.println("Logger IOException creating log file");
                ioe.printStackTrace();
            }

        }
    }



    public static void writeToConsole(String message){
        if(writeToConsole){
            System.out.println(message);
        }
    }

    public static void writeToFile(String message){
        try{
            fw.write(message);
            fw.flush();
        }catch(IOException ioe){
            System.out.println("Logger - IOException Writing To File");
            ioe.printStackTrace();
        }
    }

    public static void log(String message){
        if(writeToConsole){
            writeToConsole(message);
        }
        if(writeToFile){
            writeToFile(message);
        }
    }
}
