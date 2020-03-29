package ie.tcd.lucene.scobo.parsers.QueryDocs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Queries {
	private static String queryPath ;
	
	

    public static List<QueryObjects> queryLoader() {
        QueryObjects queryObject = new QueryObjects();
        String dummyTag = QueryXmlTags.TOP_START.getTag();
    	String topTag = QueryXmlTags.TOP_START.getTag();
        List<QueryObjects> queries = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(queryPath));
            String line;

            int counter = 0;
            while ((line = br.readLine()) != null) {
                String lineTag = checkIfDocLineHasTag(line);

                if (lineTag != null && lineTag.equals(topTag)) { 
                    counter++;
                    dummyTag = lineTag;
                    queries.add(queryObject);
                    queryObject = new QueryObjects();
                } else if (lineTag != null && !lineTag.equals(topTag)) { 
                    dummyTag = lineTag;
                }
                populateQueryFields(dummyTag, line, queryObject, counter);
            }
            queries.add(queryObject);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queries;
    }

    private static String checkIfDocLineHasTag(String docLine) {
        for (QueryXmlTags tag : QueryXmlTags.values()) {
            if (docLine.contains(tag.getTag())) {
                return tag.getTag();
            }
        }
        return null;
    }

    private static void populateQueryFields(String queryLineTag, String queryLine, QueryObjects queryObject, int counter) {
        if (queryLineTag.equals(QueryXmlTags.QUERY_NUMBER.getTag())) {
            queryObject.setQueryNumber(queryLine.replaceAll(QueryXmlTags.QUERY_NUMBER.getTag(), ""));
        } else if (queryLineTag.equals(QueryXmlTags.QUERY_TITLE.getTag())){
            queryObject.setQueryTitle(queryObject.getQueryTitle() + " " + queryLine.replaceAll(QueryXmlTags.QUERY_TITLE.getTag(),
                    ""));
        } else if (queryLineTag.equals(QueryXmlTags.QUERY_DESCRIPTION.getTag())){
            queryObject.setQueryDescription(queryObject.getQueryDescription() + " " + queryLine.replaceAll(
                    QueryXmlTags.QUERY_DESCRIPTION.getTag(), ""));
        } else if (queryLineTag.equals(QueryXmlTags.QUERY_NARRATIVE.getTag())){
            queryObject.setQueryNarrative(queryObject.getQueryNarrative() + " " + queryLine.replaceAll(
                    QueryXmlTags.QUERY_NARRATIVE.getTag(), ""));
        } else{
            queryObject.setqId(String.valueOf(counter));
        }
    }
	
}
