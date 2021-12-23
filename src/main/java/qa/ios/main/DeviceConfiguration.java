package main.java.qa.ios.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import main.java.qa.ios.util.PropertyUtils;

public class DeviceConfiguration extends TestBase{
	
	CommandPrompt cmd = new CommandPrompt();
	Map<String, String> devices = new HashMap<String, String>();
	
	public void ideviceinfo() throws Exception {
		
    	Path file = Paths.get(PropertyUtils.getProperty("ideviceinfo"));
    	Path path = Paths.get(file.toFile().toString());
		String output = cmd.runCommand(path.toString());
		String [] lines = output.split("\n");
		
		if (lines.length==1)
			System.out.println("No devices are plugged in");
			Log.info("No devices are plugged in");
		
		if (lines[1].equalsIgnoreCase("ActivationState: Activated"))
			
			System.out.println("Device is listed with ideviceinfo");
		Log.info("Device is listed with ideviceinfo");
	}
	
	public Map<String, String> getDevices() throws Exception {
		
		//ideviceinfo();
		
    	Path file = Paths.get(PropertyUtils.getProperty("ideviceinfo"));
    	Path path = Paths.get(file.toFile().toString());
		String output = cmd.runCommand(path.toString());
		
		String [] lines = output.split("\n");
		
		if(lines.length<=1) {
			System.out.println("No devices are plugged in");
			Log.info("No devices are plugged in");
			deviceCount = 0;
			//System.exit(0);	// exit if no connected devices found
		}
		
		for(int i=1;i<lines.length;i++){
			if(lines[i].contains("DeviceName")){
				DeviceName = lines[i];
								
				DeviceName=DeviceName.replaceAll("DeviceName:", "").trim();
				devices.put("deviceName", DeviceName);
				
				Log.info(lines[i]);
				
			}
			if(lines[i].contains("DeviceClass")){
				DeviceClass = lines[i];
				
				DeviceClass=DeviceClass.replaceAll("DeviceClass:", "").trim();
				devices.put("DeviceClass", DeviceClass);
				Log.info(lines[i]);
			}
			if(lines[i].contains("UniqueDeviceID")){
				UniqueDeviceID = lines[i];
				
				UniqueDeviceID=UniqueDeviceID.replaceAll("UniqueDeviceID:", "").trim();
				devices.put("UniqueDeviceID", UniqueDeviceID);
				Log.info(lines[i]);
			}
			if(lines[i].contains("ProductVersion")){
				ProductVersion = lines[i];
				
				ProductVersion=ProductVersion.replaceAll("ProductVersion:", "").trim();
				devices.put("ProductVersion", ProductVersion);
				Log.info(lines[i]);
			}
		}
		return devices;
		
	}

}
