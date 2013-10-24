package ca.kijiji.contest;

import java.util.Arrays;
import java.util.Map;

/**
 * The TrafficItem class. This will primarily have the main properties for each parking ticket.
 * There will also be some processing on location2, as we only need the street name.
 * @author Ivan
 *
 */
public class TrafficItem {

	private String tagNumberMasked;
	private int infractionCode;
	private double fineAmount;
	
	private String location2; // this is the only one that is necessary to not be null; parse this and grab address
	//private String location3;
	//private String location4;
	
	
	public TrafficItem() {
		super();
	}
	
	public TrafficItem(String[] data, Map<String, String> abbreviations,
			Map<String, String> streetNames, Map<String, String> directions) throws DataFormatException {
		if (data.length != 11)
			// Error! Throw a DataFormatException
			throw new DataFormatException("Data length inappropriate! Erroneous data for " + Arrays.toString(data));
		this.tagNumberMasked = data[0];
		try	{
			this.fineAmount = Double.parseDouble(data[4]);
		} catch (NumberFormatException nfex)	{
			throw new DataFormatException("Bad number formatting for fine amount! Erroneous data for " + Arrays.toString(data));
		}
		this.location2 = parse(data[7].trim(), abbreviations, streetNames, directions);

	}

	private String parse(String location, Map<String, String> abbreviations, Map<String, String> streetNames, Map<String, String> directions) {
		// Before parsing, remove everything that isn't the alphabet. This includes numbers, slashes, and dashes.
		// The replacement needs to be a space or everything is going to be mushed together. We will trim everything.
		String[] arr = location.split(" ");
		int j = 0;
		int len = 0;
		while (j < arr.length && streetNames.get(arr[j]) == null)	{
			arr[j] = arr[j].replaceAll("[^a-zA-Z\\ ]", "~");
			if (arr[j].contains("~"))	{
				// know it's a numeric; remove the length of it
				len += arr[j].length() + 1;
			}
			j++;
		}
		if (len < location.length())
			location = location.substring(len);
		else
			location = "";
		// Before we proceed with processing, we check with all the erroneous streets. If they haven't been filled, we 
		// add to the blank list. Add a check before processing to speed up a little.
		if (location.length() != 0)	{
			location = location.trim();
			String [] splitLoc = location.split("\\s");
			for (int i = 0; i < splitLoc.length; i++)
				splitLoc[i] = splitLoc[i].trim();
			// Go from last index, and work backwards until there are no suffixes to remove
			int i;
			if (splitLoc.length > 0)	
				i = splitLoc.length - 1;
			else
				return location;
			int endIndex = location.length();
			try	{
			while (i > 0 && (abbreviations.get(splitLoc[i]) != null || directions.get(splitLoc[i]) != null))	{
				// decrement endIndex
				endIndex -= (splitLoc[i].length()+1);
				i--;
			}} catch (ArrayIndexOutOfBoundsException aex)	{
				System.out.println(location);
				aex.printStackTrace();
			}
			return location.substring(0, endIndex);
		} else
			return "";
	}



	public String getTagNumberMasked() {
		return tagNumberMasked;
	}
	public void setTagNumberMasked(String tagNumberMasked) {
		this.tagNumberMasked = tagNumberMasked;
	}
	public int getInfractionCode() {
		return infractionCode;
	}
	public void setInfractionCode(int infractionCode) {
		this.infractionCode = infractionCode;
	}
	public double getFineAmount() {
		return fineAmount;
	}
	public void setFineAmount(double fineAmount) {
		this.fineAmount = fineAmount;
	}
	public String getLocation2() {
		return location2;
	}
	public void setLocation2(String location2) {
		this.location2 = location2;
	}


}
