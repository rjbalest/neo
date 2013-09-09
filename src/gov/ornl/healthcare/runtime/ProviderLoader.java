package gov.ornl.healthcare.runtime;

import java.util.logging.Level;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.core.CollectionLoader;

public class ProviderLoader
{
	public static void main(String args[])
	{
		String configurationURL = "config/configuration_file_1.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		CollectionLoader.clearDB();
		CollectionLoader.run();
		
		configurationURL = "config/configuration_file_2.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		CollectionLoader.run();
		
		// to run below, db setup is required
		configurationURL = "config/configuration_db.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		CollectionLoader.run();
	}
}
