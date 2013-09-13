package gov.ornl.healthcare.runtime;

import java.util.logging.Level;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.core.MongoLoader;

/**
 * 
 * @author matt lee
 * 
 */
public class CsvDb2Mongo_Run {
	public static void main(String args[]) {

		if (args[0] == null) {
			System.out.println("Error: Insufficient Arguments..");
		} else {
			String configurationURL = args[0];
			Configuration.getLogger().setLevel(Level.FINEST);
			Configuration.addConfigDocument(configurationURL);
			MongoLoader.run();
		}
	}
}
