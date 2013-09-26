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

		// processing nppes ids

		if (fieldName.equals("NPI") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_npi_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Replacement_NPI") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_npi_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Full_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_name_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Other_Organization_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_org_name_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Other_Full_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_name_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Authorized_Official_Full_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_name_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Employer_Identification_Number_EIN") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_ein_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Business_Mailing_Address") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_address_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Business_Mailing_Address_Telephone_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_phone_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Business_Mailing_Address_Fax_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_fax_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Business_Practice_Location_Address") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_address_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Business_Practice_Location_Address_Telephone_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_phone_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Full_Address") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_address_hashkey_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Full_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_name_hashkey_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Org_Name") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_org_name_hashkey_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Business_Practice_Location_Address_Fax_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_fax_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Authorized_Official_Telephone_Number") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_phone_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("Other_Provider_Identifier") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_other_id_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("State") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_state_nppes(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.equals("Provider_Taxonomy_Code") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_taxonomy_code_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}
		
		else if (fieldName.startsWith("Provider_Taxonomy") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_taxonomy_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}
		
		else if (fieldName.startsWith("Provider_License_Number_and_State") && mongoCollectionSource.equals("nppes")) {
			assigned_id = assignId_license_id_nppes(mongoCollectionSource, fieldName, raw, dbo);
		}

		// processing pecos ids

		else if (fieldName.equals("Provider_Base_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Billing_Agency_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Employer_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Member_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("IDTF_Supervising_Physician_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("IDTF_Technician_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("IDTF_Attestation_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Official_Certification_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Member_Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Base_Address") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_provider_address_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Billing_Agency_Address") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_provider_address_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Health_Home_Agency_Address") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_provider_address_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Chain_Home_Office_Address") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_provider_address_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Practice_Location_Address") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_provider_address_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Storage_Location_Address") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_provider_address_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Base_Telephone_Number") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_phone_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Billing_Agency_Telephone_Number") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_phone_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Health_Home_Agency_Telephone_Number") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_phone_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Chain_Home_Office_Telephone_Number") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_phone_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Practice_Location_Telephone_Number") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_phone_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Base_Telephone_Number") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_phone_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Telephone_Number") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_phone_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Base_Legal_Business_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Chain_Home_Office_Legal_Business_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Health_Home_Agency_Legal_Business_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Employer_Legal_Business_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Billing_Agency_Legal_Business_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Reassignment_Legal_Business_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Practice_Location_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Storage_Location_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Base_Enrollment_State") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_state_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("State") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_state_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("PAC_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_pac_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Employer_PAC_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_pac_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Reassignment_PAC_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_pac_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Member_PAC_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_pac_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Contractor_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_contractor_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Chain_Home_Office_Responsible_Contractor_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_contractor_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Enrollment_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_enrollment_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Employer_Enrollment_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_enrollment_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Member_Enrollment_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_enrollment_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Reassignment_Enrollment_ID") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_enrollment_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Base_School_Grad_Year") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_school_grad_year_specialty_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Provider_Base_Non_Physician_Specialty_Type") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_non_physician_specialty_pecos(mongoCollectionSource, fieldName, raw, dbo);

		} else if (fieldName.startsWith("Other_Provider_Identifier_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_other_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Subunit_Consolidation_Medicare_ID_Buyer_Seller_Etc") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_medicare_type_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Practice_Location_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_practice_location_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Subunit_Consolidation_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_subunit_consolidation_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Chain_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_chain_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Health_Home_Agency_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_health_home_agency_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Specialty_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_specialty_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Specialty_Code") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_specialty_code_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Reassignment_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_reassignment_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Employer_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_employer_pecos(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("Member_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_member_pecos(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("Storage_Location_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_storage_location_pecos(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("Adverse_Legal_Action") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_adverse_legal_action_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Billing_Agency_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_billing_agency_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Authorized_Delegated_Official_Certification_Statement") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_official_certification_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Ambulance_Service_Service_Area_State") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_state_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Equipment") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_equipment_pecos(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("IDTF_CPT4_HCPCS_Codes") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_hcpcs_pecos(mongoCollectionSource, fieldName, raw, dbo);
		} else if (fieldName.startsWith("IDTF_Technician_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_technician_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("IDTF_Supervising_Physician_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_physician_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("IDTF_Attestation_Info") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_attestation_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("State_License_Number_State") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_license_id_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("PAR_Status") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_par_status_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Site_Visit") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_site_visit_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Subunit_Consolidation_NPI_Buyer_Seller_Etc") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_npi_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.startsWith("Practice_Location_NPI") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_npi_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Full_Address") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_address_hashkey_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Full_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_name_hashkey_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Org_Name") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_org_name_hashkey_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Billing_Agency_TIN") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_TIN_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Chain_Home_Office_TIN") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_TIN_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Employer_TIN") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_TIN_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Health_Home_Agency_Registry_TIN") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_TIN_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Reassignment_TIN") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_TIN_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Practice_Location_Claims_System_PIN") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_pin_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else if (fieldName.equals("Practice_Location_Railroad_Board_PIN") && mongoCollectionSource.equals("pecos")) {
			assigned_id = assignId_rrb_pecos(mongoCollectionSource, fieldName, raw, dbo);
		}

		else {
			String abbr = fieldName;
			assigned_id = abbr + "::" + raw.trim().replaceAll(" ", "_");
		}
		return assigned_id;

	}

	public String assignId_other_id_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {

		String abbr = "OTHER_ID::";
		String index = fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length()) + "";
		String id = dbo.get("Other_Provider_Identifier_" + index).toString();
		String type = dbo.get("Other_Provider_Identifier_Type_Code_" + index).toString();

		try {
			if (type.trim().equals("01")) {
				abbr = "OTHER_ID::";
			}
			if (type.trim().equals("02")) {
				abbr = "UPIN::";
			}
			if (type.trim().equals("04")) {
				abbr = "MEDICARE_UNSPECIFIED::";
			}
			if (type.trim().equals("05")) {
				abbr = "MEDICAID::";
			}
			if (type.trim().equals("06")) {
				abbr = "OSCAR::";
			}
			if (type.trim().equals("07")) {
				abbr = "NSC::";
			}
			if (type.trim().equals("08")) {
				abbr = "PIN::";
			}
		} catch (Exception e) {
			System.out.println("* Warning:" + raw);
			abbr = "OTHER_ID::";
		}
		String assigned_id = abbr + id;
		return assigned_id;
	}

	public String assignId_provider_taxonomy_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		return raw;
	}

	public String assignId_ein_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "EIN::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_npi_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NPI::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_state_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ST::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_city_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "CT::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_coutry_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "CTR::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_zip_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ZIP::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_license_id_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "LCN::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	
	public String assignId_taxonomy_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "TAX::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}
	
	public String assignId_taxonomy_code_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "TAC::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_name_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "FNAM::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_org_name_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ONAM::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_fax_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "FAX::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_phone_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "TEL::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_address_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ADD::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_name_hashkey_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NAMK::";
		String[] parsed = raw.split(" ");
		String hashKey = parsed[0] + " " + parsed[parsed.length - 1];
		String assigned_id = abbr + hashKey.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_org_name_hashkey_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NAMK::";
		String[] parsed = raw.split(" ");
		String hashKey = parsed[0] + " " + parsed[parsed.length - 1];
		String assigned_id = abbr + hashKey.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_address_hashkey_nppes(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ADDK::";
		String zip = (String) dbo.get("Postal_Code");
		String assigned_id = abbr;
		int ith = 0;
		StringTokenizer lineTokenizer = new StringTokenizer(raw, " ");
		while (lineTokenizer.hasMoreTokens()) {
			String value = lineTokenizer.nextToken();
			assigned_id += value.trim();
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

	public String assignId_name_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NAM::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_name_hashkey_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NAMK::";
		String[] parsed = raw.split(" ");
		String hashKey = parsed[0] + " " + parsed[parsed.length - 1];
		String assigned_id = abbr + hashKey.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_org_name_hashkey_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ONAMK::";
		String[] parsed = raw.split(" ");
		String hashKey = parsed[0] + " " + parsed[parsed.length - 1];
		String assigned_id = abbr + hashKey.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_contractor_id_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "CON_ID::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_npi_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NPI::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_rrb_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "RRB::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_pin_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "PIN::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_pac_id_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "PAC_ID::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_enrollment_id_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ENR_ID::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_phone_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "TEL::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_org_name_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ONAM::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_state_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ST::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_non_physician_specialty_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "NPS::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_school_grad_year_specialty_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "SCY::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_practice_location_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "PL::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_subunit_consolidation_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "DPT::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_chain_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "CHN::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_health_home_agency_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "HHA::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_specialty_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "SPE::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_specialty_code_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "SPC::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_reassignment_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "RAS::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_employer_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "EMP::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_member_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "MEM::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_storage_location_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "SL::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_adverse_legal_action_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ALA::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_billing_agency_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "BILA::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_official_certification_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "CERT::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_hcpcs_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "HCPCS::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_equipment_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "EQP::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_technician_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "TECH::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_physician_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "PHY::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_attestation_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ATT::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_license_id_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "LCN::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_par_status_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "PARS::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_site_visit_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "SV::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_provider_address_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ADD::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_TIN_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "TIN::";
		String assigned_id = abbr + raw.trim().replaceAll(" ", "_");
		return assigned_id;
	}

	public String assignId_address_hashkey_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {
		String abbr = "ADDK::";
		String zip = (String) dbo.get("Postal_Code");
		if (zip == null)
			zip = (String) dbo.get("Address_Postal_Code");
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

	public String assignId_other_id_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {

		String abbr = "OTHER_ID::";
		String index = fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length()) + "";
		String id = dbo.get("Medicare_ID_" + index).toString();
		String type = dbo.get("Medicare_ID_Type_" + index).toString();

		try {
			if (type.trim().equals("OS")) {
				abbr = "OSCAR::";
			}
			if (type.trim().equals("UP")) {
				abbr = "UPIN::";
			}
			if (type.trim().equals("NS")) {
				abbr = "NSC::";
			}
			if (type.trim().equals("PN")) {
				abbr = "PIN::";
			}
			if (type.trim().equals("GP")) {
				abbr = "GPIN::";
			}
			if (type.trim().equals("EP")) {
				abbr = "EPIN::";
			}
			if (type.trim().equals("PR")) {
				abbr = "RRB::";
			}
			if (type.trim().equals("NP")) {
				abbr = "NPI::";
			}
		} catch (Exception e) {
			System.out.println("* Warning:" + raw);
			abbr = "OTHER_ID::";
		}
		String assigned_id = abbr + id;
		return assigned_id;
	}

	public String assignId_medicare_type_id_pecos(String mongoCollectionSource, String fieldName, String raw, DBObject dbo) {

		String abbr = "OTHER_ID::";
		String id = dbo.get("Subunit_Consolidation_Medicare_ID_Buyer_Seller_Etc").toString();
		String type = dbo.get("Subunit_Consolidation_Medicare_Type_Buyer_Seller_Etc").toString();

		try {
			if (type.trim().equals("OS")) {
				abbr = "OSCAR::";
			}
			if (type.trim().equals("UP")) {
				abbr = "UPIN::";
			}
			if (type.trim().equals("NS")) {
				abbr = "NSC::";
			}
			if (type.trim().equals("PN")) {
				abbr = "PIN::";
			}
			if (type.trim().equals("GP")) {
				abbr = "GPIN::";
			}
			if (type.trim().equals("EP")) {
				abbr = "EPIN::";
			}
			if (type.trim().equals("PR")) {
				abbr = "RRB::";
			}
			if (type.trim().equals("NP")) {
				abbr = "NPI::";
			}
		} catch (Exception e) {
			System.out.println("* Warning:" + raw);
			abbr = "OTHER_ID::";
		}
		String assigned_id = abbr + id;
		return assigned_id;
	}
}
