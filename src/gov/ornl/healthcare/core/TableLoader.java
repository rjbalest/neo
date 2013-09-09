/**
 * 
 */
package gov.ornl.healthcare.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.logging.Level;


import gov.ornl.healthcare.config.Configuration;
import gov.ornl.healthcare.dbutils.JDBCUtils;

/**
 * Class to load a collection of providers from a JDBC accessible database into
 * Mongo
 * 
 * @author chandola
 * @author matt lee
 * 
 */
public class TableLoader {

	public static void run() {

		long time_s, time_e;
		time_s = System.currentTimeMillis();
		Configuration.getLogger().log(Level.FINE, "Creating primary collection");

		String dbURL = Configuration.getStringValue("dbURL");
		String dbUser = Configuration.getStringValue("dbUser");
		String dbPassword = Configuration.getStringValue("dbPassword");
		String dbCreateTableQuery = Configuration.getStringValue("dbCreateTableQuery");
		String dbInsertMode = Configuration.getStringValue("dbInsertMode");
		String tableName = Configuration.getStringValue("tableName");
		String seperator = Configuration.getStringValue("seperator");

		JDBCUtils dbUtils = new JDBCUtils(dbURL, dbUser, dbPassword);
		dbUtils.initDB();
		Connection conn = dbUtils.getConnection();
		Statement batch_stmt = null;
		int numOfUpdate = 0;
		try {
			batch_stmt = conn.createStatement();

			if (dbInsertMode.equals("n")) {
				String dropSql = "DROP TABLE " + tableName;
				dbUtils.executeUpdate_IgnoreException(dropSql);
				dbUtils.executeUpdate_IgnoreException(dbCreateTableQuery);
			}

			String dataFile = Configuration.getStringValue("dataFile");
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(dataFile));
				String line;
				while ((line = br.readLine()) != null) {
					String insertSql = "INSERT INTO " + tableName + " values (";
					String rexSeperator = java.util.regex.Pattern.quote(seperator);
					line = line.replaceAll(rexSeperator, seperator + " ");
					StringTokenizer lineTokenizer = new StringTokenizer(line, seperator);
					while (lineTokenizer.hasMoreTokens()) {
						String value = lineTokenizer.nextToken().trim();
						value = "'" + value + "'";
						insertSql += value + ",";
					}
					insertSql = insertSql.substring(0, insertSql.length() - 1) + ")";
					batch_stmt.addBatch(insertSql);
					numOfUpdate++;
					if(numOfUpdate%10000==0){
						batch_stmt.executeBatch();
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
			} catch (IOException e) {
				Configuration.getLogger().log(Level.SEVERE, e.getMessage(), e);
			}
			batch_stmt.executeBatch();
			batch_stmt.close();
			dbUtils.closeDB();
		} catch (SQLException e) {
			Configuration.getLogger().log(Level.WARNING, "Batch updating SQL Exception occured. ");
		} finally {
			dbUtils.closeDB();
		}
		Configuration.getLogger().log(Level.FINE, "Finished creating primary collection");
		time_e = System.currentTimeMillis();
		System.out.println("- file loaded in " + (time_e - time_s) / 1000 + " seconds.");

	}
}
