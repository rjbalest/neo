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
import com.mongodb.Bytes;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.dbutils.JDBCUtils;
import gov.ornl.healthcare.dbutils.MongoUtils;
import gov.ornl.healthcare.standardization.EntityResolver;
import gov.ornl.healthcare.standardization.IdAssigner;

/**
 * 
 * @author chandola
 * @author matt lee
 * 
 */
public class MongoLoader {

	public static void clearDB() {
		String mongoURL = Configuration.getStringValue("mongoURL");
		int mongoPort = Configuration.getIntegerValue("mongoPort");
		String mongoDatabase = Configuration.getStringValue("mongoDatabase");
		String mongoUsername = Configuration.getStringValue("mongoUsername");
		String mongoPassword = Configuration.getStringValue("mongoPassword");

		MongoUtils mongoUtils;
		if (mongoURL == null)
			mongoUtils = new MongoUtils(mongoDatabase);
		else
			mongoUtils = new MongoUtils(mongoURL, mongoPort, mongoDatabase, mongoUsername, mongoPassword);
		mongoUtils.initDB();
		mongoUtils.dropDB(mongoDatabase);
		mongoUtils.closeDB();
	}

	public static void run() {

		String clearDB = Configuration.getStringValue("clearDB").trim();
		String primaryConfigs = Configuration.getStringValue("primary").trim();
		String supplementConfigs = Configuration.getStringValue("supplement");
		String secondaryConfigs = Configuration.getStringValue("secondary");

		if (clearDB.trim().toUpperCase().equals("Y")) {
			clearDB();
		}

		if (primaryConfigs != null) {
			// build primary
			Configuration.init();
			Configuration.getLogger().setLevel(Level.FINEST);
			String[] primaryConfigArray = primaryConfigs.split(",");
			for (String primaryConfig : primaryConfigArray) {
				primaryConfig = primaryConfig.trim();
				Configuration.init();
				Configuration.getLogger().setLevel(Level.FINEST);
				Configuration.addConfigDocument(primaryConfig);
				create_primary();
			}
		}
		// build supplements
		if (supplementConfigs != null) {
			String[] supplementConfigArray = supplementConfigs.split(",");
			for (String supplementConfig : supplementConfigArray) {
				supplementConfig = supplementConfig.trim();
				Configuration.init();
				Configuration.getLogger().setLevel(Level.FINEST);
				Configuration.addConfigDocument(supplementConfig);
				create_supplement();
			}
		}

		// build secondaries
		if (secondaryConfigs != null) {
			String[] secondaryConfigArray = secondaryConfigs.split(",");
			for (String secondaryConfig : secondaryConfigArray) {
				secondaryConfig = secondaryConfig.trim();
				Configuration.init();
				Configuration.getLogger().setLevel(Level.FINEST);
				Configuration.addConfigDocument(secondaryConfig);
				create_secondary();
			}
		}
	}

	public static void create_supplement() {
		String mongoURL = Configuration.getStringValue("mongoURL");
		int mongoPort = Configuration.getIntegerValue("mongoPort");
		String mongoDatabase = Configuration.getStringValue("mongoDatabase");
		String mongoUsername = Configuration.getStringValue("mongoUsername");
		String mongoPassword = Configuration.getStringValue("mongoPassword");

		MongoUtils mongoUtils;
		if (mongoURL == null)
			mongoUtils = new MongoUtils(mongoDatabase);
		else
			mongoUtils = new MongoUtils(mongoURL, mongoPort, mongoDatabase, mongoUsername, mongoPassword);
		mongoUtils.initDB();
		long time_s, time_e;
		time_s = System.currentTimeMillis();
		Configuration.getLogger().log(Level.FINE, "Creating primary collection");
		createSupplementInfo(mongoUtils);
		Configuration.getLogger().log(Level.FINE, "Finished creating primary collection");
		time_e = System.currentTimeMillis();

		System.out.println("- Supplment added in " + (time_e - time_s) / 1000 + " seconds.	");
		mongoUtils.closeDB();
	}

	public static void create_primary() {
		String mongoURL = Configuration.getStringValue("mongoURL");
		int mongoPort = Configuration.getIntegerValue("mongoPort");
		String mongoDatabase = Configuration.getStringValue("mongoDatabase");
		String mongoUsername = Configuration.getStringValue("mongoUsername");
		String mongoPassword = Configuration.getStringValue("mongoPassword");

		MongoUtils mongoUtils;
		if (mongoURL == null)
			mongoUtils = new MongoUtils(mongoDatabase);
		else
			mongoUtils = new MongoUtils(mongoURL, mongoPort, mongoDatabase, mongoUsername, mongoPassword);
		mongoUtils.initDB();
		long time_s, time_e;
		time_s = System.currentTimeMillis();
		Configuration.getLogger().log(Level.FINE, "Creating primary collection");
		createEntityCollection(mongoUtils);
		Configuration.getLogger().log(Level.FINE, "Finished creating primary collection");
		time_e = System.currentTimeMillis();

		System.out.println("- Primary collection created in " + (time_e - time_s) / 1000 + " seconds.	");
		mongoUtils.closeDB();
	}

	public static void create_secondary() {
		String mongoURL = Configuration.getStringValue("mongoURL");
		int mongoPort = Configuration.getIntegerValue("mongoPort");
		String mongoDatabase = Configuration.getStringValue("mongoDatabase");
		String mongoUsername = Configuration.getStringValue("mongoUsername");
		String mongoPassword = Configuration.getStringValue("mongoPassword");

		MongoUtils mongoUtils;
		if (mongoURL == null)
			mongoUtils = new MongoUtils(mongoDatabase);
		else
			mongoUtils = new MongoUtils(mongoURL, mongoPort, mongoDatabase, mongoUsername, mongoPassword);
		mongoUtils.initDB();
		long time_s, time_e;

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
		String sourceCollectionName = Configuration.getStringValue("mongoCollectionName");
		String remainAfterDisintegrate = Configuration.getStringValue("remainAfterDisintegrate");
		StringTokenizer tokenizer = new StringTokenizer(mongoCollectionFieldsToDisintegrate, ",");

		ArrayList<String> remainAfterDisintegrateList = new ArrayList<String>();
		if (remainAfterDisintegrate != null) {
			String[] remainAfterDisintegrateArray = remainAfterDisintegrate.split(",");
			for (String s : remainAfterDisintegrateArray) {
				s = s.trim();
				remainAfterDisintegrateList.add(s);
			}
		}

		ArrayList<String> fieldList = new ArrayList<String>();
		IdAssigner idAssigner = new IdAssigner();

		while (tokenizer.hasMoreTokens()) {
			String field = tokenizer.nextToken();
			field = field.trim();
			String patternOpen = "[";
			String patternClosed = "]";
			int patternOpenIdx = field.indexOf(patternOpen);
			int patternClosedIdx = field.indexOf(patternClosed);

			if (patternOpenIdx >= 0 && patternClosedIdx >= 0 && patternOpenIdx < patternClosedIdx) {
				String stringToSubstitute = field.substring(patternOpenIdx + patternOpen.length(), patternClosedIdx);
				String[] minmaxStr = stringToSubstitute.split("-");
				int min = Integer.parseInt(minmaxStr[0].trim());
				int max = Integer.parseInt(minmaxStr[1].trim());

				for (int i = min; i <= max; i++) {
					String rex = java.util.regex.Pattern.quote(patternOpen + stringToSubstitute + patternClosed);
					fieldList.add(field.replaceAll(rex, i + "").trim());
				}
			} else {
				fieldList.add(field.trim());
			}
		}

		Configuration.expand();

		if (mongoCollectionFieldsToDisintegrate != null) {

			char mongoInsertMode = Configuration.getStringValue("mongoInsertMode").charAt(0);
			if (mongoInsertMode == 'w') {
				for (int i = 0; i < fieldList.size(); i++) {
					String fieldName = fieldList.get(i);

					if (fieldName.indexOf(":") >= 0) {
						String[] parsedFiledName = fieldName.split(":");
						mongoUtils.setCollection(parsedFiledName[1].trim());
					} else {
						mongoUtils.setCollection(fieldName);
					}
					mongoUtils.clearCollection();
				}
			}
			mongoUtils.setCollection(sourceCollectionName);
			int entryNo = 0;
			DBCursor cursor = mongoUtils.getCursor();
			cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
			try {
				while (cursor.hasNext()) {
					DBObject dbo = cursor.next();
					entryNo++;
					if (entryNo % 10000 == 0) {
						System.out.println(entryNo + " entries processed.");
					}

					for (int i = 0; i < fieldList.size(); i++) {
						String docId = dbo.get("_id").toString();
						String fieldName = fieldList.get(i);
						String targetCollectionName = "";
						if (fieldName.indexOf(":") >= 0) {
							String[] parsedFieldName = fieldName.split(":");
							targetCollectionName = parsedFieldName[1].trim();
							fieldName = parsedFieldName[0].trim();
						} else {
							targetCollectionName = fieldName.trim();
						}

						if (dbo.get(fieldName) != null) {

							Object id = null;
							try {
								id = new ObjectId(docId);
							} catch (Exception e) {
								id = docId;
							}
							String edgeName = Configuration.getStringValue("edgeName:" + fieldName);
							if (edgeName == null)
								edgeName = fieldName;

							if (dbo.get(fieldName).getClass() == String.class) {
								String fieldValue = dbo.get(fieldName).toString();
								if (fieldValue == null)
									fieldValue = "";
								fieldValue = fieldValue.toString().trim();
								ArrayList<String> ref_list = new ArrayList<String>();
								ref_list.add(docId); // ok
								
								ArrayList<String> rel_types_list = new ArrayList<String>();
								rel_types_list.add("_"+edgeName + "^-1");

								if (!fieldValue.equals("")) {
									String assigned_id = idAssigner.assignId(mongoCollectionSource, fieldName, targetCollectionName, fieldValue, dbo);
									mongoUtils.setCollection(sourceCollectionName);
									BasicDBObject searchQuery = new BasicDBObject().append("_id", id);
									BasicDBObject newDocument = new BasicDBObject("$unset", new BasicDBObject().append(fieldName.trim(), 1));
									if (!remainAfterDisintegrateList.contains(fieldName.trim()))
										mongoUtils.update(searchQuery, newDocument);

									BasicDBObject doc = new BasicDBObject("_id", assigned_id).append("value", fieldValue).append("_" + edgeName + "^-1", ref_list).append("_rel_types", rel_types_list).append("_rel_ref", ref_list);

									String subFieldNames = Configuration.getStringValue("subFields:" + fieldName);
									if (subFieldNames != null) {
										String[] subFields = subFieldNames.split(",");
										for (String s : subFields) {
											s = s.trim();
											String[] parsed_s = s.split(":");
											if (dbo.get(parsed_s[0].trim()) != null) {
												doc = doc.append(parsed_s[1].trim(), dbo.get(parsed_s[0].trim()));
												searchQuery = new BasicDBObject().append("_id", id);
												newDocument = new BasicDBObject("$unset", new BasicDBObject().append(parsed_s[0].trim(), 1));
												if (!remainAfterDisintegrateList.contains(fieldName.trim()))
													mongoUtils.update(searchQuery, newDocument);

											}
										}

									}

									mongoUtils.setCollection(targetCollectionName);

									try {
										mongoUtils.addToCollection(doc);
									} catch (com.mongodb.MongoException e) {
										if (e.getCode() == 11000) {
											// if duplicate key already exists;
											// update db
											searchQuery = new BasicDBObject().append("_id", assigned_id);
											newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("_" + edgeName + "^-1", docId));
											mongoUtils.update(searchQuery, newDocument);
											
											newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("_rel_types", "_" + edgeName + "^-1"));
											mongoUtils.update(searchQuery, newDocument);
											
											newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("_rel_ref", docId));
											mongoUtils.update(searchQuery, newDocument);
											
											
										}
										// do nothing
									}

									mongoUtils.setCollection("cms.keyDictionary");
									BasicDBObject dbObject = new BasicDBObject("_id", assigned_id);
									dbObject = dbObject.append("collectionName", targetCollectionName);
									if (dbObject != null) {
										try {
											mongoUtils.addToCollection(dbObject);
										} catch (com.mongodb.MongoException e) {
											// do nothing
										}
									}

									mongoUtils.setCollection(sourceCollectionName);
									searchQuery = new BasicDBObject().append("_id", id);
									newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("_" + edgeName, assigned_id));
									mongoUtils.update(searchQuery, newDocument);
									newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("_rel_types", "_"+edgeName));
									mongoUtils.update(searchQuery, newDocument);
									newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append("_rel_ref", assigned_id));
									mongoUtils.update(searchQuery, newDocument);

								}
							} else if (dbo.get(fieldName).getClass() == com.mongodb.BasicDBList.class) {

								// do nothing

							}

						}

					}

				}
				System.out.println("* DONE: " + entryNo + " entries processed.");
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

	private static void createSupplementInfo(MongoUtils mongoUtils) {
		String source = Configuration.getStringValue("source");
		if (source.compareToIgnoreCase("db") == 0) {
			createSupplementInfoFromDB(mongoUtils);
		}
		if (source.compareToIgnoreCase("file") == 0) {
			createSupplementInfoFromFile(mongoUtils);
		}
	}

	private static void createSupplementInfoFromFile(MongoUtils mongoUtils) {
		String dataFile = Configuration.getStringValue("dataFile");

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(dataFile));
			EntityResolver resolver = new EntityResolver();
			char mongoInsertMode = Configuration.getStringValue("mongoInsertMode").charAt(0);
			String mongoCollectionName = Configuration.getStringValue("mongoCollectionName");
			String mongoCollectionFieldsList = Configuration.getStringValue("mongoCollectionFieldsList");
			String mongoCollectionFields = Configuration.getStringValue("mongoCollectionFields");
			String keyOfSource = Configuration.getStringValue("keyOfSource");
			String keyOfTargetCollection = Configuration.getStringValue("keyOfTargetCollection");
			String seperator = Configuration.getStringValue("seperator");
			String isMultiple = Configuration.getStringValue("isMultiple").toUpperCase().trim();

			String[] fieldNamesList = null;
			if (mongoCollectionFieldsList != null)
				fieldNamesList = getTokens(mongoCollectionFieldsList);
			String[] fieldNames = getTokens(mongoCollectionFields);
			HashMap<String, Integer> fieldName2Idx = new HashMap<String, Integer>();
			for (int i = 0; i < fieldNames.length; i++) {
				fieldName2Idx.put(fieldNames[i].trim(), i);
			}
			HashMap<String, Integer> indexHash = new HashMap<String, Integer>();
			mongoUtils.setCollection(mongoCollectionName);
			if (mongoInsertMode == 'w')
				mongoUtils.clearCollection();
			String line;
			int lineNo = 0;
			mongoUtils.setCollection(mongoCollectionName);
			System.out.println("* Indexing Mongo Documents: " + keyOfTargetCollection);
			mongoUtils.createIndex(new BasicDBObject(keyOfTargetCollection, 1));
			System.out.println("* Indexing DONE.");
			int index = 1;

			String concatFields = Configuration.getStringValue("concatFields");
			HashMap<String, String[]> concatInfo = new HashMap<String, String[]>();
			if (concatFields != null) {
				String[] concatFieldsArray = concatFields.split(",");
				for (String concatField : concatFieldsArray) {
					concatField = concatField.trim();
					String fieldsToMerge = Configuration.getStringValue("concatFields:" + concatField);
					String[] fieldsToMergeArray = fieldsToMerge.split(",");
					concatInfo.put(concatField, fieldsToMergeArray);
				}
			}

			String dummyFields = Configuration.getStringValue("dummyFields");
			HashMap<String, String[]> dummyInfo = new HashMap<String, String[]>();
			if (dummyFields != null) {
				String[] dummyFieldsArray = dummyFields.split(",");
				for (String dummyField : dummyFieldsArray) {
					dummyField = dummyField.trim();
					String fieldsToMerge = Configuration.getStringValue("dummyFields:" + dummyField);
					String[] fieldsToMergeArray = fieldsToMerge.split(",");
					dummyInfo.put(dummyField, fieldsToMergeArray);
				}
			}

			String ignoreFields = Configuration.getStringValue("ignoreFields");
			if (ignoreFields != null) {
				ignoreFields = ignoreFields.trim();
			}
			HashMap<String, Boolean> ifIgnore = new HashMap<String, Boolean>();
			if (ignoreFields != null) {
				String[] ignoreFieldsArray = ignoreFields.split(",");
				for (String ignoreField : ignoreFieldsArray) {
					ignoreField = ignoreField.trim();
					ifIgnore.put(ignoreField, true);
				}
			}

			ArrayList<String> toListFields = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (line.trim().equals(""))
					break;
				String[] parsed = line.split(seperator + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				String keyOfSourceValue = parsed[fieldName2Idx.get(keyOfSource)];
				keyOfSourceValue = keyOfSourceValue.replaceAll("\"", "").trim();

				BasicDBObject query = new BasicDBObject(keyOfTargetCollection, keyOfSourceValue);

				if (fieldNamesList != null) {

					for (String fieldName : fieldNamesList) {
						toListFields.add(fieldName);
						fieldName = fieldName.trim();
						if (!fieldName.equals(keyOfSource)) {

							String fieldValue = "";
							if (!concatInfo.containsKey(fieldName)) {
								fieldValue = parsed[fieldName2Idx.get(fieldName)];
							} else {
								for (String field : concatInfo.get(fieldName)) {
									field = field.trim();
									fieldValue += " " + parsed[fieldName2Idx.get(field)];
								}
							}

							if (!dummyInfo.containsKey(fieldName)) {
								fieldValue = parsed[fieldName2Idx.get(fieldName)];
							}
							if (dummyInfo.containsKey(fieldName) && !fieldValue.trim().equals("")) {
								fieldValue = new ObjectId().toString();
							}

							if (fieldValue == null)
								fieldValue = "";
							fieldValue = fieldValue.replaceAll("\"", "");
							fieldValue = resolver.resolve(fieldName, fieldValue);

							if (isMultiple.equals("Y")) {

								BasicDBObject newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append(fieldName, fieldValue));
								if (!fieldValue.equals("") && !ifIgnore.containsKey(fieldName)) {
									mongoUtils.update(query, newDocument);
								}
								index++;
							}

						}

					}
				}

				for (String fieldName : fieldNames) {
					fieldName = fieldName.trim();
					if (!fieldName.equals(keyOfSource)) {
						String fieldValue = parsed[fieldName2Idx.get(fieldName)];
						if (fieldValue == null)
							fieldValue = "";
						fieldValue = fieldValue.replaceAll("\"", "").trim();
						fieldValue = resolver.resolve(fieldName, fieldValue);
						if (isMultiple.equals("Y")) {

							if (indexHash.containsKey(keyOfSourceValue)) {
								index = indexHash.get(keyOfSourceValue);
							} else {
								index = 1;
							}

							BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName + "_" + index, fieldValue));
							if (!fieldValue.equals("") && !ifIgnore.containsKey(fieldName) && !toListFields.contains(fieldName))
								mongoUtils.update(query, newDocument);

						} else {
							BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName, fieldValue));
							if (!fieldValue.equals("") && !ifIgnore.containsKey(fieldName))
								mongoUtils.update(query, newDocument);
						}

					}
				}
				if (concatFields != null) {
					String[] concatFieldsArray = concatFields.split(",");
					for (String fieldName : concatFieldsArray) {
						fieldName = fieldName.trim();
						if (!fieldName.equals(keyOfSource)) {
							String fieldValue = "";
							if (!concatInfo.containsKey(fieldName)) {
								fieldValue = parsed[fieldName2Idx.get(fieldName)];
							} else {
								for (String field : concatInfo.get(fieldName)) {
									field = field.trim();
									fieldValue += " " + parsed[fieldName2Idx.get(field)];
								}
							}
							if (fieldValue == null)
								fieldValue = "";
							fieldValue = fieldValue.replaceAll("\"", "").trim();
							fieldValue = resolver.resolve(fieldName, fieldValue);
							if (isMultiple.equals("Y")) {

								if (indexHash.containsKey(keyOfSourceValue)) {
									index = indexHash.get(keyOfSourceValue);
								} else {
									index = 1;
								}

								BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName + "_" + index, fieldValue));
								if (!fieldValue.equals(""))
									mongoUtils.update(query, newDocument);

							} else {
								BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName, fieldValue));
								if (!fieldValue.equals(""))
									mongoUtils.update(query, newDocument);
							}

						}
					}
				}

				if (dummyFields != null) {
					String[] dummyFieldsArray = dummyFields.split(",");
					for (String fieldName : dummyFieldsArray) {
						fieldName = fieldName.trim();
						if (!fieldName.equals(keyOfSource)) {
							String fieldValue = "";
							if (!dummyInfo.containsKey(fieldName)) {
								fieldValue = parsed[fieldName2Idx.get(fieldName)];
							} else {
								for (String field : dummyInfo.get(fieldName)) {
									field = field.trim();
									fieldValue += " " + parsed[fieldName2Idx.get(field)];
								}
							}
							if (fieldValue == null)
								fieldValue = "";

							if (!fieldValue.trim().equals("")) {
								fieldValue = new ObjectId().toString();
							}

							fieldValue = fieldValue.replaceAll("\"", "").trim();
							fieldValue = resolver.resolve(fieldName, fieldValue);
							if (isMultiple.equals("Y")) {

								if (indexHash.containsKey(keyOfSourceValue)) {
									index = indexHash.get(keyOfSourceValue);
								} else {
									index = 1;
								}

								BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName + "_" + index, fieldValue));
								if (!fieldValue.equals(""))
									mongoUtils.update(query, newDocument);

							} else {
								BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName, fieldValue));
								if (!fieldValue.equals(""))
									mongoUtils.update(query, newDocument);
							}

						}
					}
				}
				indexHash.put(keyOfSourceValue, index + 1);
				lineNo++;
			}

			System.out.println("* DONE: " + lineNo + " lines processed.");

			br.close();
		} catch (FileNotFoundException e) {
			Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private static void createSupplementInfoFromDB(MongoUtils mongoUtils) {
		String dbURL = Configuration.getStringValue("dbURL");
		String dbUser = Configuration.getStringValue("dbUser");
		String dbPassword = Configuration.getStringValue("dbPassword");
		String mongoCollectionName = Configuration.getStringValue("mongoCollectionName");
		String mongoCollectionFieldsList = Configuration.getStringValue("mongoCollectionFieldsList");
		String mongoCollectionFields = Configuration.getStringValue("mongoCollectionFields");
		String keyOfSource = Configuration.getStringValue("keyOfSource");
		String keyOfTargetCollection = Configuration.getStringValue("keyOfTargetCollection");
		String isMultiple = Configuration.getStringValue("isMultiple").toUpperCase().trim();
		String[] fieldNamesList = null;
		if (mongoCollectionFieldsList != null)
			fieldNamesList = getTokens(mongoCollectionFieldsList);
		String[] fieldNames = getTokens(mongoCollectionFields);
		String dbQuery = Configuration.getStringValue("dbQuery");
		JDBCUtils dbUtils = new JDBCUtils(dbURL, dbUser, dbPassword);
		dbUtils.initDB();
		Configuration.getLogger().log(Level.FINE, "Extract Query Started");
		dbUtils.executeQuery(dbQuery);
		Configuration.getLogger().log(Level.FINE, "Extract Query Finished");
		HashMap<String, Integer> indexHash = new HashMap<String, Integer>();

		String ignoreFields = Configuration.getStringValue("ignoreFields").trim();
		HashMap<String, Boolean> ifIgnore = new HashMap<String, Boolean>();
		if (ignoreFields != null) {
			String[] ignoreFieldsArray = ignoreFields.split(",");
			for (String ignoreField : ignoreFieldsArray) {
				ignoreField = ignoreField.trim();
				ifIgnore.put(ignoreField, true);
			}
		}
		if (dbUtils.getResultset() != null) {
			mongoUtils.setCollection(mongoCollectionName);
			System.out.println("* Indexing Mongo Documents: " + keyOfTargetCollection);
			mongoUtils.createIndex(new BasicDBObject(keyOfTargetCollection, 1));
			System.out.println("* Indexing DONE.");
			ResultSet rs = dbUtils.getResultset();
			int index = 1;

			String concatFields = Configuration.getStringValue("concatFields");
			HashMap<String, String[]> concatInfo = new HashMap<String, String[]>();
			if (concatFields != null) {
				String[] concatFieldsArray = concatFields.split(",");
				for (String concatField : concatFieldsArray) {
					concatField = concatField.trim();
					String fieldsToMerge = Configuration.getStringValue("concatFields:" + concatField);
					String[] fieldsToMergeArray = fieldsToMerge.split(",");
					concatInfo.put(concatField, fieldsToMergeArray);
				}
			}

			try {

				while (rs.next()) {
					BasicDBObject query = new BasicDBObject(keyOfTargetCollection, rs.getString(keyOfSource));

					if (fieldNamesList != null) {
						for (String fieldName : fieldNamesList) {
							fieldName = fieldName.trim();
							if (!fieldName.equals(keyOfSource)) {

								String fieldValue = "";
								if (!concatInfo.containsKey(fieldName)) {
									fieldValue = rs.getString(fieldName);
								} else {
									for (String field : concatInfo.get(fieldName)) {
										field = field.trim();
										fieldValue += " " + rs.getString(fieldName);
									}
								}
								if (isMultiple.equals("Y")) {
									BasicDBObject newDocument = new BasicDBObject().append("$addToSet", new BasicDBObject().append(fieldName, fieldValue));
									if (!fieldValue.equals("") && !ifIgnore.containsKey(fieldName))
										mongoUtils.update(query, newDocument);
								}

							}
						}
					}

					for (String fieldName : fieldNames) {
						fieldName = fieldName.trim();
						if (!fieldName.equals(keyOfSource)) {
							if (isMultiple.equals("Y")) {

								if (indexHash.containsKey(rs.getString(keyOfSource))) {
									index = indexHash.get(rs.getString(keyOfSource));
								} else {
									index = 1;
								}

								BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName + "_" + index, rs.getString(fieldName)));
								if (!rs.getString(fieldName).equals("") && !ifIgnore.containsKey(fieldName))
									mongoUtils.update(query, newDocument);
							} else {
								BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName, rs.getString(fieldName)));
								if (!rs.getString(fieldName).equals("") && !ifIgnore.containsKey(fieldName))
									mongoUtils.update(query, newDocument);
							}

						}
					}
					if (concatFields != null) {
						String[] concatFieldsArray = concatFields.split(",");
						for (String fieldName : concatFieldsArray) {
							fieldName = fieldName.trim();
							if (!fieldName.equals(keyOfSource)) {
								String fieldValue = "";
								if (!concatInfo.containsKey(fieldName)) {
									fieldValue = rs.getString(fieldName);
								} else {
									for (String field : concatInfo.get(fieldName)) {
										field = field.trim();
										fieldValue += " " + rs.getString(fieldName);
									}
								}
								if (isMultiple.equals("Y")) {

									if (indexHash.containsKey(rs.getString(keyOfSource))) {
										index = indexHash.get(rs.getString(keyOfSource));
									} else {
										index = 1;
									}

									BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName + "_" + index, fieldValue));
									mongoUtils.update(query, newDocument);
								} else {
									BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append(fieldName, fieldValue));
									mongoUtils.update(query, newDocument);
								}

							}
						}
					}
					indexHash.put(rs.getString(keyOfSource), index + 1);

				}
			} catch (SQLException e) {
				Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		dbUtils.closeDB();
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
			int entryNo = 0;
			ResultSet rs = dbUtils.getResultset();
			try {
				while (rs.next()) {
					BasicDBObject dbObject = createMongoObject(rs, fieldNames);
					if (dbObject != null) {
						mongoUtils.addToCollection(dbObject);
					}
					entryNo++;
					if (entryNo % 10000 == 0) {
						System.out.println(entryNo + " entries processed.");
					}
				}
				System.out.println("DONE: " + entryNo + " entries processed.");
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
			HashMap<String, Integer> fieldName2Idx = new HashMap<String, Integer>();
			for (int i = 0; i < fieldNames.length; i++) {
				fieldName2Idx.put(fieldNames[i].trim(), i);
			}
			if (mongoInsertMode == 'w')
				mongoUtils.clearCollection();
			String line;
			int lineNo = 0;
			while ((line = br.readLine()) != null) {

				mongoUtils.setCollection(mongoCollectionName);
				BasicDBObject dbObject = createMongoObject(line, fieldNames, fieldName2Idx);
				if (dbObject != null) {
					mongoUtils.addToCollection(dbObject);
					mongoUtils.setCollection("cms.keyDictionary");
					String id = dbObject.getString("_id");
					dbObject = new BasicDBObject("_id", id);
					dbObject = dbObject.append("collectionName", mongoCollectionName);
					if (dbObject != null) {
						mongoUtils.addToCollection(dbObject);
					}
				}

				lineNo++;
				if (lineNo == 100)
					break;
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
			String fieldGroup = Configuration.getStringValue("fieldGrouping:" + fieldNames[i].trim());
			// System.out.println(fieldGroup);
			if (fieldGroup != null) {
				ArrayList<String> list = new ArrayList<String>();
				ht.put(fieldGroup, list);
			}
		}

		String concatFields = Configuration.getStringValue("concatFields");

		if (concatFields != null) {
			String[] concatFieldsArray = concatFields.split(",");
			for (String concatField : concatFieldsArray) {
				concatField = concatField.trim();
				String fieldGroup = Configuration.getStringValue("fieldGrouping:" + concatField);
				if (fieldGroup != null) {
					ArrayList<String> list = new ArrayList<String>();
					ht.put(fieldGroup, list);
				}
			}

			for (int i = 0; i < concatFieldsArray.length; i++) {
				String concatField = concatFieldsArray[i].trim();
				try {
					String fieldsToMerge = Configuration.getStringValue("concatFields:" + concatField);
					String[] fields = fieldsToMerge.split(",");
					String value = "";

					for (String field : fields) {
						field = field.trim();
						if (!value.equals("<UNAVAIL>"))
							value = value + " " + rs.getString(field);

					}
					String fieldGroup = Configuration.getStringValue("fieldGrouping:" + concatField);
					value = resolver.resolve(concatField, value);

					if (fieldGroup == null) {

						if (!value.equals("")) {
							dbObject = dbObject.append(concatField, value);
						}
					} else {

						try {

							if (!value.trim().equals("")) {
								dbObject = dbObject.append(concatField, value);
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
					Configuration.getLogger().log(Level.WARNING, "Could not extract field " + concatField);
				}
			}
		}
		for (int i = 0; i < fieldNames.length; i++) {
			String field = fieldNames[i];
			try {
				String value = rs.getString(field);
				if (value.equals("<UNAVAIL>"))
					value = "";
				String fieldGroup = Configuration.getStringValue("fieldGrouping:" + field);
				value = resolver.resolve(field, value);

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
			key = key.trim();
			dbObject = dbObject.append(key, ht.get(key));
		}
		return dbObject;
	}

	private static BasicDBObject createMongoObject(String line, String[] fieldNames, HashMap<String, Integer> fieldName2Idx) {

		String mongoCollectionSource = Configuration.getStringValue("mongoCollectionSource");

		String ignoreFields = Configuration.getStringValue("ignoreFields");
		if (ignoreFields != null) {
			ignoreFields = ignoreFields.trim();
		}
		HashMap<String, Boolean> ifIgnore = new HashMap<String, Boolean>();
		if (ignoreFields != null) {
			String[] ignoreFieldsArray = ignoreFields.split(",");
			for (String ignoreField : ignoreFieldsArray) {
				ignoreField = ignoreField.trim();
				ifIgnore.put(ignoreField, true);
			}
		}

		String seperator = Configuration.getStringValue("seperator");
		if (mongoCollectionSource == null || mongoCollectionSource.isEmpty())
			mongoCollectionSource = "NA";
		String rexSeperator = java.util.regex.Pattern.quote(seperator);
		line = line.replaceAll(rexSeperator, seperator + " ");

		BasicDBObject dbObject = new BasicDBObject("source", mongoCollectionSource);
		EntityResolver resolver = new EntityResolver();
		HashMap<String, ArrayList<String>> ht = new HashMap<String, ArrayList<String>>();

		for (int i = 0; i < fieldNames.length; i++) {
			String fieldGroup = Configuration.getStringValue("fieldGrouping:" + fieldNames[i].trim());
			// System.out.println(fieldGroup);
			if (fieldGroup != null) {
				ArrayList<String> list = new ArrayList<String>();
				ht.put(fieldGroup, list);
			}
		}

		int i = 0;
		String[] tokens = line.split(seperator + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		if (fieldName2Idx.size() != tokens.length) {
			System.out.println("* Warning (wrong data format): " + line);
			return null;
		}

		// System.out.println(line);
		String concatFields = Configuration.getStringValue("concatFields");

		if (concatFields != null) {
			String[] concatFieldsArray = concatFields.split(",");

			for (String concatField : concatFieldsArray) {
				concatField = concatField.trim();
				String fieldGroup = Configuration.getStringValue("fieldGrouping:" + concatField);
				if (fieldGroup != null) {
					ArrayList<String> list = new ArrayList<String>();
					ht.put(fieldGroup, list);
				}
			}

			for (String concatField : concatFieldsArray) {
				// System.out.println("* " + concatField);
				concatField = concatField.trim();
				String fieldsToMerge = Configuration.getStringValue("concatFields:" + concatField);
				String[] fields = fieldsToMerge.split(",");
				String value = "";

				for (String field : fields) {
					field = field.trim();
					// System.out.println(field);
					if (!value.equals("<UNAVAIL>")) {
						value = value + " " + tokens[fieldName2Idx.get(field)].replaceAll("\"", "").trim();
					}
				}

				String fieldGroup = Configuration.getStringValue("fieldGrouping:" + concatField);

				value = resolver.resolve(concatField, value);

				if (fieldGroup == null) {
					if (!value.equals("")) {
						dbObject = dbObject.append(concatField, value);
					}
				} else {

					try {

						if (!value.trim().equals("")) {
							dbObject = dbObject.append(concatField, value);
						}
						if (!value.trim().equals("")) {
							dbObject = dbObject.append(fieldGroup, value);
							ArrayList<String> list = ht.get(fieldGroup);
							list.add(value.trim());
						}

					} catch (Exception e) {

					}
				}

			}
		}

		String dummyFields = Configuration.getStringValue("dummyFields");

		if (dummyFields != null) {
			String[] dummyFieldsArray = dummyFields.split(",");

			for (String dummyField : dummyFieldsArray) {
				dummyField = dummyField.trim();
				String fieldGroup = Configuration.getStringValue("fieldGrouping:" + dummyField);
				if (fieldGroup != null) {
					ArrayList<String> list = new ArrayList<String>();
					ht.put(fieldGroup, list);
				}
			}

			for (String dummyField : dummyFieldsArray) {
				// System.out.println("* " + concatField);
				dummyField = dummyField.trim();
				String fieldsToMerge = Configuration.getStringValue("dummyFields:" + dummyField);
				String[] fields = fieldsToMerge.split(",");
				String value = "";

				for (String field : fields) {
					field = field.trim();
					// System.out.println(field);
					if (!value.equals("<UNAVAIL>")) {
						value = value + " " + tokens[fieldName2Idx.get(field)].replaceAll("\"", "").trim();
					}
				}

				String fieldGroup = Configuration.getStringValue("fieldGrouping:" + dummyField);

				value = resolver.resolve(dummyField, value);

				if (!value.trim().equals("")) {
					value = new ObjectId().toString();
				}
				if (fieldGroup == null) {
					if (!value.equals("")) {
						dbObject = dbObject.append(dummyField, value);
					}
				} else {

					try {

						if (!value.trim().equals("")) {
							dbObject = dbObject.append(dummyField, value);
						}
						if (!value.trim().equals("")) {
							dbObject = dbObject.append(fieldGroup, value);
							ArrayList<String> list = ht.get(fieldGroup);
							list.add(value.trim());
						}

					} catch (Exception e) {

					}
				}

			}
		}

		for (String t : tokens) {
			t = t.trim();
			t = t.replaceAll("\"", "").trim();
			String field = fieldNames[i];
			i++;
			String value = t;
			if (value.equals("<UNAVAIL>"))
				value = "";

			String fieldGroup = Configuration.getStringValue("fieldGrouping:" + field);

			value = resolver.resolve(field, value);

			if (fieldGroup == null) {

				if (!value.trim().equals("") && !ifIgnore.containsKey(field)) {
					// System.out.println(value);
					dbObject = dbObject.append(field, value);
				}
			} else {

				try {

					if (!value.trim().equals("") && !ifIgnore.containsKey(field)) {
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
			key = key.trim();
			dbObject = dbObject.append(key, ht.get(key));
		}

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
