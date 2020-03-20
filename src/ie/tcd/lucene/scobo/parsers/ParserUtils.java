package ie.tcd.lucene.scobo.parsers;

import ie.tcd.lucene.scobo.Constants;

public class ParserUtils {
	private ParserUtils() { }
	
	public static String getLineMarker(String line) {
		for (String marker : Constants.CONTENT_MARKERS) {
			if(line.contains(marker))
				return marker;
		}
		return null;
	}
}
