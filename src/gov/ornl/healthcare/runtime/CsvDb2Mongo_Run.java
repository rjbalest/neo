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
		String configurationURL = "config/configuration_db_nppes_primary.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		MongoLoader.clearDB();
		MongoLoader.create_primary();

		configurationURL = "config/configuration_db_nppes_secondary_provider.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		MongoLoader.create_secondary();

		configurationURL = "config/configuration_db_nppes_secondary_practice_address.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		MongoLoader.create_secondary();

		configurationURL = "config/configuration_db_nppes_secondary_license_state.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		MongoLoader.create_secondary();
	}
}
