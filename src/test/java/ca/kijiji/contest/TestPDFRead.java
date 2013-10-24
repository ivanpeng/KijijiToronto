package ca.kijiji.contest;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class TestPDFRead {

	@Test
	public void test() {
		String regex = "[^a-zA-Z\\ ]";
		String str = "112A ROSEWOOD AVE";
		str = str.replaceAll(regex, "~");
		String [] splitLoc = str.split("\\s");
		if (splitLoc[0].contains("~"))	{
			str = str.substring(splitLoc[0].length()+1);
			splitLoc = str.split("\\s");
		}
		System.out.println(str);
	}
	
	@Test
	public void test2()	{
		StreetDao dao = new TorontoStreetDao();
		Map<String, String> abbreviations = dao.processAbbreviations();
		Map<String, String> directions = dao.processDirections();
		Map<String, String> streetNames = dao.processStreetNames();
		String[] data = new String[]{"***78746,20120101,192,STAND SIGNED TRANSIT STOP,60,0000,NR,355 PARKSIDE DR,,,ON"};
		String[] test1 = data[0].split("\\s");
		try {
			TrafficItem item = new TrafficItem(test1, abbreviations, streetNames, directions);
			System.out.println(item.getLocation2());
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
	}

}
