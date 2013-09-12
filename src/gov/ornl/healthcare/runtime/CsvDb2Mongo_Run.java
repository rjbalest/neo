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
		
		String configurationURL = "config/nppes_db.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		MongoLoader.run();
		
		//String configurationURL = "config/nppes_file.xml";
		//Configuration.getLogger().setLevel(Level.FINEST);
		//Configuration.addConfigDocument(configurationURL);
		//MongoLoader.run();
	}
}
