package gov.ornl.healthcare.standardization;

public class EntityResolver {

	public EntityResolver() {
		// do nothing for now
	}

	/**
	 * 
	 * @param raw
	 * @return
	 */
	public String resolveAddress(String raw) {
		if(raw==null) raw="";
		String resolved = raw.toUpperCase().trim();
		return resolved;
	}

	/**
	 * 
	 * @param raw
	 * @return
	 */
	public String resolvePhoneNumber(String raw) {
		if(raw==null) raw="";
		String resolved = raw.toUpperCase().trim();
		resolved = resolved.replaceAll( "[^\\d]", "" );
		return resolved;
	}

	public String resolveOthers(String raw) {
		if(raw==null) raw="";
		String resolved = raw.trim();
		return resolved;
	}
}
