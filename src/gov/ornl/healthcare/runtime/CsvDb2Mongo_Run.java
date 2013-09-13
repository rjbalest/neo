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

		String configurationURL = "config/simple_example.xml";
		// String configurationURL = "config/nppes_csv.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		MongoLoader.run();
	}
}
