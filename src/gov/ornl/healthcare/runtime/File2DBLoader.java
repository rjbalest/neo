package gov.ornl.healthcare.runtime;

import java.util.logging.Level;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.core.TableLoader;
/**
 * 
 * @author matt lee
 * 
 */
public class File2DBLoader {

	public static void main(String args[]) {
		//load NPPES csv into RDB
		String configurationURL = "config/config_nppes_db_load.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		TableLoader.run();

		//configurationURL = "config/file2db_file_2.xml";
		//Configuration.getLogger().setLevel(Level.FINEST);
		//Configuration.addConfigDocument(configurationURL);
		//TableLoader.run();
	}
}
