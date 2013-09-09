package gov.ornl.healthcare.runtime;

import java.util.logging.Level;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.core.TableLoader;

public class File2DBLoader {

	public static void main(String args[]) {
		String configurationURL = "config/file2db_file_1.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		TableLoader.run();
		
		configurationURL = "config/file2db_file_2.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		TableLoader.run();
	}
}
