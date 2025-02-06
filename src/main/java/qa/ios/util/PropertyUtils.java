package qa.ios.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import org.testng.Assert;
import qa.ios.base.TestBase;

public class PropertyUtils extends TestBase {

	private static Properties p = new Properties();

	static {
		String workingDir = System.getProperty("user.dir");

		try {
			loadPropertyFile(workingDir + File.separator + proprtyFile);

		} catch (FileNotFoundException realCause) {

			Assert.fail("Unable to load file!", realCause);

		} catch (IOException realCause) {

			Assert.fail("Unable to load file!", realCause);
		}
	}

	public static void loadPropertyFile(String propertyFileName)
			throws FileNotFoundException, IOException {
		InputStream is;

		is = new FileInputStream(new File(propertyFileName));
		DataInputStream in = new DataInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		p.load(br);
		System.out.println(propertyFileName + " is loaded.");
		Log.info(propertyFileName + " is loaded.");
		in.close();
		br.close();
	}

	public static String getProperty(String propertyKey) {
		String propertyValue = p.getProperty(propertyKey.trim());

		if (propertyValue == null || propertyValue.trim().length() == 0)

		{

			Log.info(propertyKey + " is missing!");


		}

		return propertyValue;
	}

	public static void setProperty(String propertyKey, String value)
			throws FileNotFoundException, IOException {
		p.setProperty(propertyKey, value);
	}

	public static void listProperties() throws FileNotFoundException,
			IOException {

		for (Enumeration<?> e = p.propertyNames(); e.hasMoreElements();)

			while (e.hasMoreElements()) {
				String propertyKey = (String) e.nextElement();
				System.out.println(propertyKey + " -- "
						+ p.getProperty(propertyKey));

			}
	}
}
