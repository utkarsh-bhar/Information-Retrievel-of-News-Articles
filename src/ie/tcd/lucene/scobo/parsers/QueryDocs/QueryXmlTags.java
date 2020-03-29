package ie.tcd.lucene.scobo.parsers.QueryDocs;

public enum QueryXmlTags {
	  
	TOP_START("<top>"), TOP_END("</top>"), QUERY_NUMBER("<num> Number:"), QUERY_TITLE("<title>"), QUERY_DESCRIPTION(
	            "<desc> Description:"), QUERY_NARRATIVE("<narr> Narrative:");

		
		String tag;
	
	  QueryXmlTags(final String tag) {
	        this.tag = tag;
	    }
	  
	  public String getTag() {
	        return this.tag;
	    }
}
