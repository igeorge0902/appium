package qa.ios.testng;

import org.apache.log4j.Logger;
import org.testng.Reporter;

/**
* Throws custom exception message, and logs it, too.
*  
* @return "Huston baj van!"
*/

public class CustomException extends Exception {

	private static final long serialVersionUID = 6446869692704936034L;
	private static Logger Log = Logger.getLogger(Logger.class.getName());	 	


	
	public String toString (Exception e)  {
		Reporter.log(getLocalizedMessage()); 
		Log.info(e.getStackTrace());
		return "Huston baj van!";
		
	}

}
