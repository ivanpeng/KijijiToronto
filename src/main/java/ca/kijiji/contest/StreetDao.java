package ca.kijiji.contest;

import java.util.Map;

/**
 * We are going to use a DAO interface, to grab the list of street names and abbreviations. The
 * goal of the DAO is to encapsulate the data extraction method. For the example of Toronto,
 * there is public data available of all street names and abbreviations via a pdf file. Although
 * probably not the case with every city, this interface will encapsulate all that, as core
 * functionality will remain the same throughout.
 * 
 * @author Ivan
 *
 */
public interface StreetDao {

	public Map<String, String> processDirections();
	
	public Map<String, String> processAbbreviations();
	
	public Map<String, String> processStreetNames();
	
}
