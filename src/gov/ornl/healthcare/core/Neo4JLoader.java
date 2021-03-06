/**
 * 
 */
package gov.ornl.healthcare.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.bson.types.ObjectId;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.dbutils.JDBCUtils;
import gov.ornl.healthcare.dbutils.MongoUtils;
import gov.ornl.healthcare.dbutils.Neo4JUtils;
import gov.ornl.healthcare.standardization.EntityResolver;
import gov.ornl.healthcare.standardization.IdAssigner;

/**
 * 
 * @author chandola
 * @author matt lee
 * 
 */
public class Neo4JLoader {

    private static enum RelTypes implements RelationshipType {
	KNOWS
	    }

    public static void run() {

	/* Load providers from Mongo
	 */
	String mongoURL = Configuration.getStringValue("mongoURL");
	int mongoPort = Configuration.getIntegerValue("mongoPort");
	String mongoDatabase = Configuration.getStringValue("mongoDatabase");
	String mongoUser = Configuration.getStringValue("mongoUsername";
	String mongoPass = Configuration.getStringValue("mongoPassword");

	MongoUtils mongo;
	if (mongoURL == null)
	    mongo = new MongoUtils(mongoDatabase);
	else
	    mongo = new MongoUtils(mongoURL, mongoPort, mongoDatabase, mongoUser, mongoPass);
	mongo.initDB();
	long time_s, time_e;
	time_s = System.currentTimeMillis();

	String collectionName = "providers";
	mongo.setCollection(collectionName);

	DBCursor cursor = mongo.getCursor();
	cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
	int entryNo = 0;
	try {
	    System.out.println("Building Nodes from Mongo Documents");
	    while ( cursor.hasNext() ) {
		DBObject dbo = cursor.next();
		entryNo++;
		if (entryNo % 10000 == 0) {
		    System.out.println(entryNo + " documents processed");
		}
		// Create Node
		String licenseId = dbo.get("license_id").toString();
		System.out.println("  -- Got provider license id: " + licenseId);
	    }
	} catch (com.mongodb.MongoException e) {
	    System.out.println("Caught MongoException processing document");
	} finally {
	    cursor.close();
	    mongo.closeDB();
	}
	System.out.println("* DONE: " + entryNo + " documents processed");

    }
    
    public void create_node() {

	Neo4JUtils neo4j_util = new Neo4JUtils();

	neo4j_util.initDB();
	/* neo4j_util.clearDb();
	 */
		
	GraphDatabaseService graphDb = neo4j_util.getGraphDb();
	Node firstNode;
	Node secondNode;
	Relationship relationship;

	// START SNIPPET: transaction
	Transaction tx = graphDb.beginTx();

	// Database operations go here
	// END SNIPPET: transaction
	// START SNIPPET: addData
	firstNode = graphDb.createNode();
	firstNode.setProperty("message", "Hello, ");
	secondNode = graphDb.createNode();
	secondNode.setProperty("message", "World!");
	System.out.println(firstNode.toString());
	System.out.println(secondNode.toString());
	
	relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
	relationship.setProperty("message", "brave Neo4j ");
	System.out.println(relationship.toString());
	// END SNIPPET: addData
	
	// START SNIPPET: readData
	System.out.print(firstNode.getProperty("message"));
	System.out.print(relationship.getProperty("message"));
	System.out.print(secondNode.getProperty("message"));
	// END SNIPPET: readData
	
	String greeting = ((String) firstNode.getProperty("message")) + ((String) relationship.getProperty("message")) + ((String) secondNode.getProperty("message"));
	System.out.println(greeting);
	
	// START SNIPPET: transaction
	tx.success();
	
	// Save the data
	tx.finish();
	
	// END SNIPPET: transaction
	neo4j_util.shutDown();
    }

    public static void run_example() {

	Neo4JUtils neo4j_util = new Neo4JUtils();
	neo4j_util.initDB();
	/* neo4j_util.clearDb();
	 */
		
	GraphDatabaseService graphDb = neo4j_util.getGraphDb();
	Node firstNode;
	Node secondNode;
	Relationship relationship;

	// START SNIPPET: transaction
	Transaction tx = graphDb.beginTx();

	// Database operations go here
	// END SNIPPET: transaction
	// START SNIPPET: addData
	firstNode = graphDb.createNode();
	firstNode.setProperty("message", "Hello, ");
	secondNode = graphDb.createNode();
	secondNode.setProperty("message", "World!");
	System.out.println(firstNode.toString());
	System.out.println(secondNode.toString());
	
	relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
	relationship.setProperty("message", "brave Neo4j ");
	System.out.println(relationship.toString());
	// END SNIPPET: addData
	
	// START SNIPPET: readData
	System.out.print(firstNode.getProperty("message"));
	System.out.print(relationship.getProperty("message"));
	System.out.print(secondNode.getProperty("message"));
	// END SNIPPET: readData
	
	String greeting = ((String) firstNode.getProperty("message")) + ((String) relationship.getProperty("message")) + ((String) secondNode.getProperty("message"));
	System.out.println(greeting);
	
	// START SNIPPET: transaction
	tx.success();
	
	// Save the data
	tx.finish();
	
	// END SNIPPET: transaction
	neo4j_util.shutDown();
    }

}
