 package ca.kijiji.contest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregation;
import net.sf.jagg.Aggregator;
import net.sf.jagg.SumAggregator;

public class ParkingTicketsStats {
	
	public static final String CSV_DELIMITER = ",";

	/**
	 * This is the main function that I'll be using to code in. The main bit of processing I do is with 
	 * jagg. I chose jagg for providing functionality of aggregation against multiple properties, not only
	 * against the street names, at ease of the programmer. I'm inclined to believe that aggregation against
	 * other properties such as infraction codes might be more efficient in determining what the most
	 * profitable streets. Furthermore, jagg has multithreading properties which will be useful.
	 * 
	 * @param parkingTicketsStream
	 * @return
	 */
    public static SortedMap<String, Integer> sortStreetsByProfitability(InputStream parkingTicketsStream) {
    	/*
    	 *  Algorithm:
    	 *  1. First, parse data from InputStream to a list of TrafficItems, encapsulating all properties of parking tickets
    	 *  2. Use an aggregation tool, probably jagg, to aggregate the prices by street number property.
    	 *  3. Can check aggregator with count, total, stdev as well
    	 */
    	
    	// For parsing the InputStream, we will use Scanner to best utilize the Java Library
    	// Should encapsulate the fact that parkingTicketsStream is csv, but not the point
    	List<TrafficItem> parkingTicketData = new ArrayList<TrafficItem>();
		Scanner scanner = new Scanner(parkingTicketsStream).useDelimiter("\\n");
		// Process maps of abbreviations and street names beforehand so we don't do it every item iteration
		StreetDao dao = new TorontoStreetDao();
		Map<String, String> abbreviations = dao.processAbbreviations();
		Map<String, String> directions = dao.processDirections();
		Map<String, String> streetNames = dao.processStreetNames();
		// loop through reading data
		// Using this specific example, we know the first line is headers, so we skip first
		scanner.next();
		while (scanner.hasNext())	{
			String line = scanner.next();
			String[] data = line.trim().split(CSV_DELIMITER);
			try	{
				TrafficItem item = new TrafficItem(data, abbreviations, streetNames, directions);
				parkingTicketData.add(item);
				
			} catch (DataFormatException daex)	{
				// print stack trace for now; find some way to properly catch this error later.
				daex.printStackTrace();
			}
		}
		scanner.close();
    	// Now that we have the data, aggregate! We will use JAGG
		// List properties and aggregators beforehand.
		
		List<String> properties = new ArrayList<String>();
		properties.add("location2");
		
		// These are the list of aggregators; we are just going to use sum, but the other stats may be nice.
		List<Aggregator> aggregators = new ArrayList<Aggregator>();
		//aggregators.add(new CountAggregator("*"));
		//aggregators.add(new AvgAggregator("fineAmount"));
		//aggregators.add(new StdDevAggregator("fineAmount"));
		aggregators.add(new SumAggregator("fineAmount"));
		
		// Set the aggregation builder, with the aggregator list
		Aggregation aggregation = new Aggregation.Builder()
			.setAggregators(aggregators)
			.setParallelism(4)
			.setProperties(properties)
			.build();
		List<AggregateValue<TrafficItem>> aggregateValues = aggregation.groupBy(parkingTicketData);
		
		// Now we have our collapsed/aggregated list by street name, we format for sorted map.
		SortedMap<String, Integer> sortedMap = new TreeMap<String,Integer>();
		// Get sum aggregator
		Aggregator finalAggregator = aggregators.get(0);
		for (AggregateValue<TrafficItem> aggregateValue: aggregateValues)	{
			TrafficItem item = aggregateValue.getObject();
			String str = aggregateValue.getAggregateValue(finalAggregator).toString();
			int i = (int) Double.parseDouble(str);
			sortedMap.put(item.getLocation2(), i);
		}
		
		System.out.println(sortedMap);
    	return sortedMap;
    }
}