package gov.ornl.healthcare.standardization;

import gov.ornl.healthcare.config.Configuration;

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
		String fieldGroup = Configuration.getStringValue("fieldGrouping:" + fieldName);
		if (fieldGroup != null)
			fieldName = fieldGroup;

		if (fieldName.endsWith("Full_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_name_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("Organization_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_org_name_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("NPI") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_npi_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("Provider_Business_Mailing_Address") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_mailing_address_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("Provider_Business_Practice_Location_Address") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_practice_address_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("_Fax_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_fax_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("_Taxonomy_Code") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_taxonomy_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("Provider_License_Number_and_State") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_license_id_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("Other_Provider_Identifier_Info") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_other_id_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.endsWith("_Telephone_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_phone_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else {
			String abbr = fieldName;
			assigned_id = raw.toUpperCase().trim().replaceAll(" ", "_");
		}
		return assigned_id;

	}

	public String assignId_other_id_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "other_id::";
		String[] parsed = raw.split(" ");
		String id = parsed[0];
		if(parsed[1].trim().equals("01")){
			abbr="other_id::";
		}
		if(parsed[1].trim().equals("02")){
			abbr="medicare_upin::";
		}
		if(parsed[1].trim().equals("04")){
			abbr="medicare_unspecified::";
		}
		if(parsed[1].trim().equals("05")){
			abbr="medicaid::";
		}
		if(parsed[1].trim().equals("06")){
			abbr="medicare_oscar::";
		}
		if(parsed[1].trim().equals("07")){
			abbr="medicase_nsc::";
		}
		if(parsed[1].trim().equals("08")){
			abbr="medicare_pin::";
		}
		String assigned_id = abbr+id;
		return assigned_id;
	}
	
	public String assignId_npi_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "npi::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_license_id_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "license_id::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_taxonomy_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "taxonomy::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_name_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "name::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_org_name_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "org_name::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_fax_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "fax::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_phone_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "phone::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_mailing_address_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "address::";
		String zip = (String) dbo.get("Provider_Business_Mailing_Address_Postal_Code");
		String assigned_id = abbr;
		int ith = 0;
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

	public String assignId_practice_address_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "address::";
		String zip = (String) dbo.get("Provider_Business_Practice_Location_Address_Postal_Code");
		String assigned_id = abbr + "::";
		int ith = 0;
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
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public String assignId_address_fake_file_2(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = fieldName;
		String assigned_id = "";

		String[] array = raw.split(" ");
		String zip = array[array.length - 1];
		if (!isInteger(zip)) {
			zip = "00000";
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
