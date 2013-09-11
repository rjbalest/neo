package gov.ornl.healthcare.runtime;

import java.util.logging.Level;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.core.MongoLoader;
import gov.ornl.healthcare.core.Neo4JLoader;
/**
 * 
 * @author matt lee
 * 
 */
public class Mongo2Neo_Run
{
	public static void main(String args[])
	{
		String configurationURL = "config/neo4j_nppes.xml";
		Configuration.getLogger().setLevel(Level.FINEST);
		Configuration.addConfigDocument(configurationURL);
		Neo4JLoader.run();
	}
}
