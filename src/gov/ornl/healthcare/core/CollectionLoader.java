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

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.dbutils.JDBCUtils;
import gov.ornl.healthcare.dbutils.MongoUtils;
import gov.ornl.healthcare.standardization.EntityResolver;
import gov.ornl.healthcare.standardization.IdAssigner;

/**
 * Class to load a collection of providers from a JDBC accessible database into
 * Mongo
 * 
 * @author chandola
 * @author matt lee
 * 
 */
public class CollectionLoader {

	public static void clearDB() {
		String mongoURL = Configuration.getStringValue("mongoURL");
		int mongoPort = Configuration.getIntegerValue("mongoPort");
		String mongoDatabase = Configuration.getStringValue("mongoDatabase");

		MongoUtils mongoUtils;
		if (mongoURL == null)
			mongoUtils = new MongoUtils(mongoDatabase);
		else
			mongoUtils = new MongoUtils(mongoURL, mongoPort, mongoDatabase);
		mongoUtils.initDB();
		mongoUtils.dropDB(mongoDatabase);
		mongoUtils.closeDB();
	}

	public static void run() {
		String mongoURL = Configuration.getStringValue("mongoURL");
		int mongoPort = Configuration.getIntegerValue("mongoPort");
		String mongoDatabase = Configuration.getStringValue("mongoDatabase");

		MongoUtils mongoUtils;
		if (mongoURL == null)
			mongoUtils = new MongoUtils(mongoDatabase);
		else
			mongoUtils = new MongoUtils(mongoURL, mongoPort, mongoDatabase);
		mongoUtils.initDB();
		long time_s, time_e;
		time_s = System.currentTimeMillis();
		Configuration.getLogger().log(Level.FINE, "Creating primary collection");
		createEntityCollection(mongoUtils);
		Configuration.getLogger().log(Level.FINE, "Finished creating primary collection");
		time_e = System.currentTimeMillis();

		System.out.println("- Primary collection created in " + (time_e - time_s) / 1000 + " seconds.	");

		time_s = System.currentTimeMillis();
		Configuration.getLogger().log(Level.FINE, "Creating secondary collections");
		createSecondaryCollection(mongoUtils);
		Configuration.getLogger().log(Level.FINE, "Finished creating secondary collections");
		time_e = System.currentTimeMillis();
		System.out.println("- Secondary collections created in " + (time_e - time_s) / 1000 + " seconds.");
		mongoUtils.closeDB();
	}

	private static void createSecondaryCollection(MongoUtils mongoUtils) {
		String mongoCollectionSource = Configuration.getStringValue("mongoCollectionSource");
		String mongoCollectionFieldsToDisintegrate = Configuration.getStringValue("mongoCollectionFieldsToDisintegrate");
		String collectionName = Configuration.getStringValue("mongoCollectionName");
		StringTokenizer tokenizer = new StringTokenizer(mongoCollectionFieldsToDisintegrate, ",");
		ArrayList<String> fieldList = new ArrayList<String>();
		IdAssigner idAssigner = new IdAssigner();

		while (tokenizer.hasMoreTokens()) {
			String field = tokenizer.nextToken();
			fieldList.add(field.trim());
		}

		if (mongoCollectionFieldsToDisintegrate != null) {

			char mongoInsertMode = Configuration.getStringValue("mongoInsertMode").charAt(0);
			if (mongoInsertMode == 'w') {
				for (int i = 0; i < fieldList.size(); i++) {
					String fieldName = fieldList.get(i);
					mongoUtils.setCollection(fieldName);
					mongoUtils.clearCollection();
				}
			}

			mongoUtils.setCollection(collectionName);

			DBCursor cursor = mongoUtils.getCursor();
			try {
				while (cursor.hasNext()) {
					DBObject dbo = cursor.next();

					for (int i = 0; i < fieldList.size(); i++) {
						String docId = dbo.get("_id").toString();
						String fieldName = fieldList.get(i);
						if (dbo.get(fieldName) != null) {

							if (dbo.get(fieldName).getClass() == String.class) {
								String fieldValue = dbo.get(fieldName).toString();
								if (fieldValue == null)
									fieldValue = "";
								fieldValue = fieldValue.toString().trim();
								String fieldGroup = Configuration.getStringValue("fieldGrouping:" + fieldName);
								if (fieldGroup != null)
									mongoUtils.setCollection(fieldGroup);
								else
									mongoUtils.setCollection(fieldName);
								ArrayList<String> ref_list = new ArrayList<String>();
								ref_list.add(docId); // ok

								if (!fieldValue.equals("")) {
									String assigned_id = idAssigner.assignId(mongoCollectionSource, fieldName, fieldValue, dbo);
									BasicDBObject doc = new BasicDBObject("_id", assigned_id).append("value", fieldValue).append("ref", ref_list);

									String subFieldNames = Configuration.getStringValue("subFields:" + fieldName);
									if (subFieldNames != null) {
										String[] subFields = subFieldNames.split(",");
										for (String s : subFields) {
											String[] parsed_s = s.split(":");
											if (dbo.get(parsed_s[0].trim()) != null)
												doc = doc.append(parsed_s[1].trim(), dbo.get(parsed_s[0].trim()));
										}

									}

									try {
										mongoUtils.addToCollection(doc);
									} catch (com.mongodb.MongoException e) {
										if (e.getCode() == 11000) {
											// if duplicate key already exists;
											// update db
											BasicDBObject searchQuery = new BasicDBObject().append("_id", assigned_id);
											BasicDBObject newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("ref", docId));

											mongoUtils.update(searchQuery, newDocument);
										}
										// do nothing
									}

									ObjectId id = new ObjectId(docId);
									mongoUtils.setCollection(collectionName);
									BasicDBObject searchQuery = new BasicDBObject().append("_id", id);
									if (fieldGroup != null)
										fieldName = fieldGroup;
									BasicDBObject newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("ref", fieldName + " | " + assigned_id ));
									mongoUtils.update(searchQuery, newDocument);
								}
							} else if (dbo.get(fieldName).getClass() == com.mongodb.BasicDBList.class) {

								// do nothing
								/*
								 * BasicDBList list = (BasicDBList)
								 * dbo.get(fieldName); for (int j = 0; j <
								 * list.size(); j++) { String fieldValue =
								 * list.get(j).toString(); String assigned_id =
								 * idAssigner.assignId(mongoCollectionSource,
								 * fieldName, fieldValue, dbo);
								 * 
								 * fieldValue = fieldValue.toString().trim();
								 * mongoUtils.setCollection(fieldName);
								 * ArrayList<String> ref_list = new
								 * ArrayList<String>(); ref_list.add(docId); //
								 * ok if (!fieldValue.trim().equals("")) {
								 * BasicDBObject doc = new BasicDBObject("_id",
								 * assigned_id).append("value",
								 * fieldValue).append("ref", ref_list);
								 * 
								 * System.out.println("subFields:" + fieldName);
								 * String subFieldNames =
								 * Configuration.getStringValue("subFields:" +
								 * fieldName); if (subFieldNames != null) {
								 * String[] subFields =
								 * subFieldNames.split(","); for (String s :
								 * subFields) { doc = doc.append(s, dbo.get(s));
								 * }
								 * 
								 * } try { mongoUtils.addToCollection(doc); }
								 * catch (com.mongodb.MongoException e) { if
								 * (e.getCode() == 11000) { // if duplicate key
								 * already // exists; // update db BasicDBObject
								 * searchQuery = new
								 * BasicDBObject().append("_id", assigned_id);
								 * BasicDBObject newDocument = new
								 * BasicDBObject().append("$push", new
								 * BasicDBObject().append("ref", docId));
								 * 
								 * mongoUtils.update(searchQuery, newDocument);
								 * } // do nothing }
								 * 
								 * ObjectId id = new ObjectId(docId);
								 * mongoUtils.setCollection(collectionName);
								 * BasicDBObject searchQuery = new
								 * BasicDBObject().append("_id", id);
								 * BasicDBObject newDocument = new
								 * BasicDBObject().append("$push", new
								 * BasicDBObject().append("ref", assigned_id));
								 * mongoUtils.update(searchQuery, newDocument);
								 * } }
								 */
							}
						}
					}
				}
			} finally {
				cursor.close();
			}
		}
	}

	private static void createEntityCollection(MongoUtils mongoUtils) {
		String source = Configuration.getStringValue("source");
		if (source.compareToIgnoreCase("db") == 0) {
			createEntityCollectionFromDB(mongoUtils);
		}
		if (source.compareToIgnoreCase("file") == 0) {
			createEntityCollectionFromFile(mongoUtils);
		}
	}

	private static void createEntityCollectionFromDB(MongoUtils mongoUtils) {
		String dbURL = Configuration.getStringValue("dbURL");
		String dbUser = Configuration.getStringValue("dbUser");
		String dbPassword = Configuration.getStringValue("dbPassword");
		char mongoInsertMode = Configuration.getStringValue("mongoInsertMode").charAt(0);
		String mongoCollectionName = Configuration.getStringValue("mongoCollectionName");
		String mongoCollectionFields = Configuration.getStringValue("mongoCollectionFields");
		String[] fieldNames = getTokens(mongoCollectionFields);
		String dbQuery = Configuration.getStringValue("dbQuery");
		JDBCUtils dbUtils = new JDBCUtils(dbURL, dbUser, dbPassword);
		dbUtils.initDB();
		Configuration.getLogger().log(Level.FINE, "Extract Query Started");
		dbUtils.executeQuery(dbQuery);
		Configuration.getLogger().log(Level.FINE, "Extract Query Finished");
		if (dbUtils.getResultset() != null) {
			mongoUtils.setCollection(mongoCollectionName);
			if (mongoInsertMode == 'w')
				mongoUtils.clearCollection();

			ResultSet rs = dbUtils.getResultset();
			try {
				while (rs.next()) {
					BasicDBObject dbObject = createMongoObject(rs, fieldNames);
					if (dbObject != null) {
						mongoUtils.addToCollection(dbObject);
					}
				}
			} catch (SQLException e) {
				Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		dbUtils.closeDB();
	}

	private static void createEntityCollectionFromFile(MongoUtils mongoUtils) {
		String dataFile = Configuration.getStringValue("dataFile");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(dataFile));
			char mongoInsertMode = Configuration.getStringValue("mongoInsertMode").charAt(0);
			String mongoCollectionName = Configuration.getStringValue("mongoCollectionName");
			String mongoCollectionFields = Configuration.getStringValue("mongoCollectionFields");
			String[] fieldNames = getTokens(mongoCollectionFields);

			mongoUtils.setCollection(mongoCollectionName);
			if (mongoInsertMode == 'w')
				mongoUtils.clearCollection();
			String line;
			int lineNo = 0;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				line = line.replaceAll("\"", "");
				BasicDBObject dbObject = createMongoObject(line, fieldNames);
				if (dbObject != null) {
					mongoUtils.addToCollection(dbObject);
				}
				lineNo++;
				if (lineNo % 10000 == 0) {
					System.out.println(lineNo + " lines processed.");
				}
			}
			System.out.println("DONE: " + lineNo + " lines processed.");

			br.close();
		} catch (FileNotFoundException e) {
			Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private static BasicDBObject createMongoObject(ResultSet rs, String[] fieldNames) {
		String mongoCollectionSource = Configuration.getStringValue("mongoCollectionSource");
		if (mongoCollectionSource == null || mongoCollectionSource.isEmpty())
			mongoCollectionSource = "NA";
		BasicDBObject dbObject = new BasicDBObject("source", mongoCollectionSource);
		EntityResolver resolver = new EntityResolver();
		HashMap<String, ArrayList<String>> ht = new HashMap<String, ArrayList<String>>();

		for (int i = 0; i < fieldNames.length; i++) {
			String fieldGroup = Configuration.getStringValue("fieldGrouping:" + fieldNames[i]);
			// System.out.println(fieldGroup);
			if (fieldGroup != null) {
				ArrayList<String> list = new ArrayList<String>();
				ht.put(fieldGroup, list);
			}
		}
		for (int i = 0; i < fieldNames.length; i++) {
			String field = fieldNames[i];
			try {
				String value = rs.getString(field);
				String fieldGroup = Configuration.getStringValue("fieldGrouping:" + field);
				 if (field.endsWith("_Address")) {
				 value = resolver.resolveAddress(value);
				 } else if (field.endsWith("_Number")) {
				 value = resolver.resolveNumber(value);
				 } else {
				value = resolver.resolveOthers(value);
				 }
				if (fieldGroup == null) {

					if (!value.equals("")) {
						dbObject = dbObject.append(field, value);
					}
				} else {

					try {

						if (!value.trim().equals("")) {
							dbObject = dbObject.append(field, value);
						}
						if (!value.trim().equals("")) {
							dbObject = dbObject.append(fieldGroup, value);
							ArrayList<String> list = ht.get(fieldGroup);
							list.add(value.trim());
						}

					} catch (Exception e) {

					}

				}
			} catch (SQLException e) {
				Configuration.getLogger().log(Level.WARNING, "Could not extract field " + field);
			}
		}
		for (String key : ht.keySet()) {
			dbObject = dbObject.append(key, ht.get(key));
		}
		dbObject = dbObject.append("ref", new ArrayList<String>());
		return dbObject;
	}

	private static BasicDBObject createMongoObject(String line, String[] fieldNames) {
		String mongoCollectionSource = Configuration.getStringValue("mongoCollectionSource");
		String seperator = Configuration.getStringValue("seperator");
		if (mongoCollectionSource == null || mongoCollectionSource.isEmpty())
			mongoCollectionSource = "NA";
		String rexSeperator = java.util.regex.Pattern.quote(seperator);
		line = line.replaceAll(rexSeperator, seperator + " ");

		StringTokenizer lineTokenizer = new StringTokenizer(line, seperator);
		BasicDBObject dbObject = new BasicDBObject("source", mongoCollectionSource);
		EntityResolver resolver = new EntityResolver();
		HashMap<String, ArrayList<String>> ht = new HashMap<String, ArrayList<String>>();

		for (int i = 0; i < fieldNames.length; i++) {
			String fieldGroup = Configuration.getStringValue("fieldGrouping:" + fieldNames[i]);
			// System.out.println(fieldGroup);
			if (fieldGroup != null) {
				ArrayList<String> list = new ArrayList<String>();
				ht.put(fieldGroup, list);
			}
		}

		int i = 0;
		String[] tokens = line.split(seperator + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		for (String t : tokens) {
			if (t.equals("<UNAVAIL>"))
				t = "";
			t = t.replaceAll("\"", "");
			String field = fieldNames[i];
			i++;
			String value = lineTokenizer.nextToken();

			String fieldGroup = Configuration.getStringValue("fieldGrouping:" + field);

			 if (field.endsWith("_Address")) {
			 value = resolver.resolveAddress(value);
			 } else if (field.endsWith("_Number")) {
			value = resolver.resolveNumber(value);
			 } else {
			value = resolver.resolveOthers(value);
			 }

			if (fieldGroup == null) {

				if (!value.trim().equals("")) {
					dbObject = dbObject.append(field, value);
				}
			} else {

				try {

					if (!value.trim().equals("")) {
						dbObject = dbObject.append(field, value);
					}

					if (!value.trim().equals("")) {
						dbObject = dbObject.append(fieldGroup, value.trim());
						ArrayList<String> list = ht.get(fieldGroup);
						list.add(value.trim());
					}

				} catch (Exception e) {

				}

			}
		}

		for (String key : ht.keySet()) {
			dbObject = dbObject.append(key, ht.get(key));
		}

		dbObject = dbObject.append("ref", new ArrayList<String>());
		return dbObject;
	}

	private static String[] getTokens(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		String[] tokens = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens[i] = tokenizer.nextToken().trim();
			i++;
		}
		return tokens;
	}
}
