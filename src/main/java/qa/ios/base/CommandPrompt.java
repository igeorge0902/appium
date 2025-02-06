package qa.ios.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandPrompt {
	
	Process p;
	ProcessBuilder builder;
	
	public String runCommand(String command) throws InterruptedException, IOException {
		
		p = Runtime.getRuntime().exec(command);
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line = "";
		String allLine = "";
		
		while ((line=r.readLine()) !=null) {
			
			allLine=allLine+""+line+"\n";
			if (line.contains("Console LogLevel: debug"))
				
				break;
		}
		return allLine;		
	}

}
