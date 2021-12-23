package main.java.qa.ios.util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
 
public class SystemCommands {
 
    public static void main(String args[]) {
 
        String s = null;
 
        try {
             
        // run the Unix "ps -ef" command
            // using the Runtime exec method:
        	Path file = Paths.get("/usr/local/bin/ideviceinfo");
        	Path path = Paths.get(file.toFile().toString());
            Process p = Runtime.getRuntime().exec(path.toString());
             
            BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
 
            BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));
 
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println("stdInput "+s);
            }
             
            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println("stdError "+s);
            }
             
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}