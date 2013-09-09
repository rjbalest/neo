package gov.ornl.healthcare.standardization;

import java.util.StringTokenizer;

import com.mongodb.DBObject;

public class IdAssigner {

	public IdAssigner() {
		// do nothing for now
	}

	/**
	 * 
	 * @param raw
	 * @return
	 */
	public String assignId(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {

		String assigned_id = "";
		if (fieldName.endsWith("address") && mongoCollectionSource.equals("fake_db")) {
			assigned_id = assignId_address_fake_db(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("address") && mongoCollectionSource.equals("fake_file_1")) {
			assigned_id = assignId_address_fake_file_1(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("address") && mongoCollectionSource.equals("fake_file_2")) {
			assigned_id = assignId_address_fake_file_2(mongoCollectionSource, fieldName, raw, dbo);
		} else {
			String abbr = fieldName;
			assigned_id = abbr + "::" + raw.toUpperCase().trim().replaceAll(" ", "_");
		}
		return assigned_id;

	}

	public String assignId_address_fake_db(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = fieldName;
		String assigned_id = "";
		String zip = (String) dbo.get("zip");
		if (zip == null)
			zip = "UNKNOWN";
		else
			zip = zip.trim();
		int ith = 0;
		assigned_id = abbr + "::";
		StringTokenizer lineTokenizer = new StringTokenizer(raw, " ");
		while (lineTokenizer.hasMoreTokens()) {
			String value = lineTokenizer.nextToken();
			assigned_id += value.toUpperCase().trim();
			ith++;
			if (ith == 2)
				break;
		}
		assigned_id += zip;
		return assigned_id;
	}

	public String assignId_address_fake_file_1(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = fieldName;
		String assigned_id = "";
		String zip = (String) dbo.get("zip");
		if (zip == null)
			zip = "00000";
		else
			zip = zip.trim();
		int ith = 0;
		assigned_id = abbr + "::";
		StringTokenizer lineTokenizer = new StringTokenizer(raw, " ");
		while (lineTokenizer.hasMoreTokens()) {
			String value = lineTokenizer.nextToken();
			assigned_id += value.toUpperCase().trim();
			ith++;
			if (ith == 2)
				break;
		}
		assigned_id += zip;
		return assigned_id;
	}

	public boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public String assignId_address_fake_file_2(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = fieldName;
		String assigned_id = "";

		String[] array = raw.split(" ");
		String zip =array[array.length-1];
		if(!isInteger(zip)){
			zip="00000";
		}
		int ith = 0;
		assigned_id = abbr + "::";
		StringTokenizer lineTokenizer = new StringTokenizer(raw, " ");
		while (lineTokenizer.hasMoreTokens()) {
			String value = lineTokenizer.nextToken();
			assigned_id += value.toUpperCase().trim();
			ith++;
			if (ith == 2)
				break;
		}
		assigned_id += zip;
		return assigned_id;
	}
}
