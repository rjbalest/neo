package gov.ornl.healthcare.runtime;

import java.util.logging.Level;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.core.CollectionLoader;

public class ProviderLoader
{
	public static void main(String args[])
	{
		String configurationURL = "config/config_csv2db_nppes.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		CollectionLoader.clearDB();
		//CollectionLoader.run();
		
		configurationURL = "config/configuration_csv_example.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		//CollectionLoader.run();
		
		// to run below, db setup is required
		configurationURL = "config/configuration_db_nppes.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		CollectionLoader.run();
	}
}
