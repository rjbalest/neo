/**
 * 
 */
package gov.ornl.healthcare.dbutils;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

/**
 * 
 * @author chandola
 * 
 */
public class Neo4JUtils {

    /* private final String DB_PATH = "db/neo4j_db";
     */
	private final String DB_PATH = "/home/oracle/Desktop/Neo4j/neo4j-community-1.9.3/data/graph.db";
	GraphDatabaseService graphDb = null;
	ExecutionEngine engine = null;

	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	public void initDB() {
		// START SNIPPET: startDb
		System.out.println();
        System.out.println( "Initializing database ..." );
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        engine = new ExecutionEngine(graphDb);

		registerShutdownHook(graphDb);
		// END SNIPPET: startDb

		// START SNIPPET: transaction
		Transaction tx = graphDb.beginTx();
		try {
			// do nothing
			// START SNIPPET: transaction
			tx.success();
		} finally {
			tx.finish();
		}
		// END SNIPPET: transaction
	}

	public void clearDb()
    {
        try
        {
            FileUtils.deleteRecursively( new File( DB_PATH ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
	
	public void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // START SNIPPET: shutdownServer
        graphDb.shutdown();
        // END SNIPPET: shutdownServer
    }
    
	private void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}
