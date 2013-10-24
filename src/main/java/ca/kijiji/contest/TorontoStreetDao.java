package ca.kijiji.contest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

/**
 * The specific implementation for Toronto's data. Data is on Toronto's website 
 * for all street names in PDF format. Due to PDFBox's ridiculous slow time, it 
 * turns out it's easier to just copy-paste the text into .txt files, and then
 * read that...
 * @author Ivan
 *
 */
public class TorontoStreetDao implements StreetDao {

	public static final String TORONTO_DATA_PATH = "src/test/resources/city-wide_index.pdf";
	public static final String TORONTO_DATA_PATH_ABBR = "src/test/resources/city-wide_index_abbreviations.txt";
	public static final String TORONTO_DATA_PATH_STREETS = "src/test/resources/city-wide_index_streets.txt";
	
	
	/**
	 * This function will return a list of all abbreviations. However, due to Toronto's available data
	 * being in PDF form and PDF parsing is a nightmare, this will be quite manual. The file is in
	 * the resources folder and we will parse page 7 from it. Unfortunately, this will change at every
	 * update of the text file...
	 * We are returning a map over a list for quick indexing. This isn't a heavy task to keep in memory
	 * and will be called at every iteration of processing, so we will keep it there. The value on each
	 * key will simply be 1, and we will check for not null.
	 */
	@Override
	public Map<String, String> processAbbreviations() {
		Map<String, String> abbreviations = new TreeMap<String,String> ();
		File file = new File(TORONTO_DATA_PATH_ABBR);
		long startTime = System.currentTimeMillis();
		try	{
			BufferedReader br = new BufferedReader(new FileReader(file));
			// Read the first line to skip
			br.readLine();
			String str = br.readLine();
			while (str != null)	{
				String[] strarr = str.split("\\s");
				for (String elem: strarr)
					abbreviations.put(elem.toUpperCase(), "1");
				str = br.readLine();
			}
			br.close();
			long totalTime = System.currentTimeMillis() - startTime;
		} catch(IOException iex)	{
			// Do something here
			System.out.println("IOEXception in processing the abbreviations.");
			iex.printStackTrace();
		} finally	{
		}
		System.out.println(abbreviations);
		return abbreviations;
	}

	/**
	 * This file parses all the street names from Toronto's open street data. 
	 * Unfortunately, the only data available is via pdf, so it's easier and 
	 * quicker to copy-and-paste manually. However, ideally, we would get the
	 * street names of Toronto via some open maps API.
	 */
	@Override
	public Map<String, String> processStreetNames() {
		Map<String, String> streetNames = new TreeMap<String, String> ();
		Map<String, String> abbreviations = processAbbreviations();
		Map<String, String> directions = processDirections();
		long startTime = System.currentTimeMillis();
		File file = new File(TORONTO_DATA_PATH_STREETS);
		try	{
			//Set up buffered reader
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = br.readLine();
			while (str != null)	{
				String strarr[] = str.split("\\s");
				// From the file pattern, we know at least the first two words are part of the street name.
				// We check after the third one if the word is all upper case. if not, then we know we've hit the end.
				// Kind of counterintuitive
				int i = 2;
				int length;
				try	{
					length = strarr[0].length() + strarr[1].length() + 1; // add 1 for the space between the two words
				} catch (ArrayIndexOutOfBoundsException aex)	{
					length = str.length();
				}
				while (i < strarr.length && StringUtils.isAllUpperCase(strarr[i]))	{
					length += strarr[i].length() + 1;
					i++;
				}
				// Remove the suffixes before doing so. This is assuming that abbreviations and directions have already been called.
				String fullStreetName = str.substring(0,length).toUpperCase();
				String strarr2[] = fullStreetName.split("\\s");
				int j = strarr2.length-1;
				int length2 = fullStreetName.length();
				while (j > 0 && (directions.get(strarr2[j]) != null || abbreviations.get(strarr2[j]) != null))	{
					length2 = length2 - (strarr2[j].length() + 1);
					j--;
				}
				streetNames.put(fullStreetName.substring(0,length2), "1");
				str = br.readLine();
				
			}
			br.close();
		} catch(IOException iex)	{
			System.out.println("IOException in processing the street names");
			iex.printStackTrace();
		}
		long totalTime = System.currentTimeMillis()-startTime;
		System.out.println(totalTime);
		return streetNames;
	}

	@Override
	public Map<String, String> processDirections() {
		// Final map which we can be a bit lazy and manual on this
		Map<String, String> directions = new TreeMap<String, String>();
		directions.put("N", "1");
		directions.put("E", "1");
		directions.put("S", "1");
		directions.put("W", "1");
		directions.put("NORTH", "1");
		directions.put("EAST", "1");
		directions.put("SOUTH", "1");
		directions.put("WEST", "1");
		return directions;
	}
	

}
