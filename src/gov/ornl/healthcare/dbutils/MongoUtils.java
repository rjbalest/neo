/**
 * 
 */
package gov.ornl.healthcare.dbutils;

import gov.ornl.healthcare.config.Configuration;

import java.net.UnknownHostException;
import java.util.logging.Level;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MapReduceCommand;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * 
 * @author chandola
 * 
 */
public class MongoUtils {
	private String url = "localhost";
	private int port = 27017;
	private String database;
	private MongoClient client = null;
	private DB db = null;
	private DBCollection collection;

	public MongoUtils(String database) {
		this.database = database;
	}

	public MongoUtils(String url, int port, String database) {
		this.url = url;
		this.port = port;
		this.database = database;
	}

	public void initDB() {
		try {
			client = new MongoClient(url, port);
		} catch (UnknownHostException e) {
			Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return;
		}
		db = client.getDB(database);
	}
	
	public void dropDB(String db) {
		System.out.println("Drop database:"+db);
		client.dropDatabase(db);
	}

	public DBCursor getCursor() {
		if (collection != null) {
			DBCursor cursor = collection.find();
			return cursor;
		} else
			return null;
	}

	public void setCollection(String collectionName) {
		collection = this.db.getCollection(collectionName);
	}

	public void clearCollection() {
		if (collection != null)
			collection.remove(new BasicDBObject());
	}

	public long getCollectionCount(String collectionName) {
		if (collection != null)
			return collection.count();
		else
			return 0;
	}

	public void closeDB() {
		if (client != null)
			client.close();
	}

	public void addToCollection(BasicDBObject dbObject) {
		if (collection != null)
			collection.insert(dbObject);
	}

	public WriteResult update(BasicDBObject searchQuery, BasicDBObject newDocument){
		return collection.update(searchQuery, newDocument);
	}
	public void createFieldCollection(String field, String collectionName) {
		if (collection != null) {
			String m = "function() { key = this." + field + ";" + "emit(\"" + field + "::\"+key.toLowerCase().trim().replace(/ /gi, \"_\"), key.trim());}";
			String r = "function(key, values) {return values[0];}";
			System.out.println(m);
			System.out.println(r);

			MapReduceCommand cmd = new MapReduceCommand(collection, m, r, collectionName, MapReduceCommand.OutputType.MERGE, null);
			collection.mapReduce(cmd);
		}
	}

	public boolean hasCollection(String field) {
		return db.collectionExists(field);
	}

	public void mergeCollections(String first, String second) {
		if (db.collectionExists(first) && db.collectionExists(second)) {
			DBCollection firstCollection = db.getCollection(first);
			DBCollection secondCollection = db.getCollection(second);
			DBCursor l = secondCollection.find();
			while (l.hasNext()) {
				BasicDBObject lObj = (BasicDBObject) l.next();
				BasicDBObject fObj = new BasicDBObject(first + "_text", lObj.get(first + "_text"));
				if (firstCollection.find(fObj) == null)
					firstCollection.insert(fObj);
			}
		}
	}

	public void dropCollection(String collectionName) {
		db.getCollection(collectionName).drop();
	}
}
