package gov.ornl.healthcare.standardization;
/**
 * 
 * @author chandola
 * @author matt lee
 * 
 */
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
		String resolved = resolveOthers(raw.trim());
		return resolved;
	}

	/**
	 * 
	 * @param raw
	 * @return
	 */
	public String resolveNumber(String raw) {
		if(raw==null) raw="";
		String resolved = resolveOthers(raw.trim());
		resolved = resolved.replaceAll( "[^\\d]", "" );
		return resolved;
	}

	public String resolveOthers(String raw) {
		if(raw==null) raw="";
		String resolved = raw.trim().replaceAll(" +", " ");
		return resolved;
	}
}
