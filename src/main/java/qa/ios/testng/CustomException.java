package qa.ios.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

/**
 * Throws custom exception message, and logs it, too.
 *
 * @return "Huston baj van!"
 */
public class CustomException extends Exception {

	private static final long serialVersionUID = 6446869692704936034L;
	private static final Logger log = LoggerFactory.getLogger(CustomException.class);

	public String toString(Exception e) {
		Reporter.log(getLocalizedMessage());
		log.info("Exception occurred", e);
		return "Huston baj van!";
	}

}
