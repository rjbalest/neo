package gov.ornl.healthcare.standardization;

/**
 * 
 * @author matt lee
 * 
 */
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
	public String assignId(String mongoCollectionSource, String fieldName, String targetCollection, String raw, DBObject dbo) {

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
		} else if (fieldName.endsWith("Provider_License_Number_State_Code") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_license_id_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("State") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_state_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("Provider_Business_Mailing_Address") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_mailing_address_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("Provider_Business_Practice_Location_Address") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_practice_address_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.endsWith("_Fax_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_fax_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("Healthcare_Provider_Taxonomy_Code") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_taxonomy_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("Provider_License_Number_and_State") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_license_id_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("Other_Provider_Identifier_Info") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_other_id_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Employer_Identification_Number_EIN")){
			assigned_id = assignId_ein_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.endsWith("_Telephone_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_phone_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else {
			String abbr = fieldName;
			assigned_id = abbr + "::" + raw.toUpperCase().trim().replaceAll(" ", "_");
		}
		return assigned_id;

	}

	public String assignId_other_id_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {

		String abbr = "OTHER_ID::";
		String[] parsed = raw.split(" ");
		String id = parsed[0];

		try {
			if (parsed[1].trim().equals("01")) {
				abbr = "OTHER_ID::";
			}
			if (parsed[1].trim().equals("02")) {
				abbr = "MEDICARE_UPIN::";
			}
			if (parsed[1].trim().equals("04")) {
				abbr = "MEDICARE_UNSPECIFIED::";
			}
			if (parsed[1].trim().equals("05")) {
				abbr = "MEDICAID::";
			}
			if (parsed[1].trim().equals("06")) {
				abbr = "MEDICARE_OSCAR::";
			}
			if (parsed[1].trim().equals("07")) {
				abbr = "MEDICARE_NSC::";
			}
			if (parsed[1].trim().equals("08")) {
				abbr = "MEDICARE_PIN::";
			}
		} catch (Exception e) {
			System.out.println("* Warning:" + raw);
			abbr = "OTHER_ID::";
		}
		String assigned_id = abbr + id;
		return assigned_id;
	}

	public String assignId_ein_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "EIN	::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}
	
	public String assignId_npi_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NPI::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_state_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "State::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_license_id_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "License::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_taxonomy_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "Taxonomy_Code::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_name_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "Full_Name::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_org_name_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "Org_Name::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_fax_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "Fax::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_phone_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "Phone::";
		String assigned_id = abbr + raw.toUpperCase().trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_mailing_address_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "Address::";
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
		String abbr = "Address::";
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

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}
}
