package main.java.qa.android.main;
/**
 * Command Prompt - this class contains method to run windows and mac commands  
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandPrompt {
	
	Process p;
	ProcessBuilder builder;
	
	/**
	 * This method run command on windows and mac
	 * @param command to run  
	 */
	
	public String runCommand(String command) throws InterruptedException, IOException
	{
		
		try {
		p = Runtime.getRuntime().exec(command);
			
			} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
			
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line="";
		String allLine="";
		
		while((line=r.readLine()) != null){
		
			allLine=allLine+""+line+"\n";
			
			if(line.contains("Console LogLevel: debug"))
			
				break;
		}
		
		return allLine;
	}
}
