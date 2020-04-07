package ie.tcd.lucene.scobo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import ie.tcd.lucene.scobo.parsers.QueryDocs.QueryObjects;

public class QueryEngine {
	private static Logger LOGGER = Logger.getLogger(QueryEngine.class.getName());

	private Similarity scoring;
	private Analyzer analyzer;
	private IndexReader reader;
	private IndexSearcher searcher;
	private Map<String, Float> boostMap;
	private QueryParser queryParser;
	
	public IndexSearcher getSearcher() {
		return searcher;
	}
	
	public QueryEngine(Directory indexDir, Analyzer analyzer, Similarity scoring) {
		try {
			this.reader = DirectoryReader.open(indexDir);
			this.searcher = new IndexSearcher(reader);
			this.analyzer = analyzer;
			this.scoring = scoring;
			this.boostMap = this.buildBoostMap();
			this.queryParser = new MultiFieldQueryParser(new String[]{"headline", "text"}, this.analyzer, this.boostMap);
		} catch (IOException e) {
			LOGGER.severe("Unable to initialize QueryEngine");
			LOGGER.severe(e.getMessage());
		}
	}
	
	public ScoreDoc[] executeQuery(Query query, int paginationCount) {
		try {
			searcher.setSimilarity(scoring);

			TopDocs docs = searcher.search(query, paginationCount);
			ScoreDoc[] hits = docs.scoreDocs;
			return hits;			
		} catch (IOException e) {
			LOGGER.severe("Failed to execute query '" + query + "'");
			LOGGER.severe(e.getMessage());
		}
		
		return null;
	}
	
	public BooleanQuery buildQuery(QueryObjects queryObj) throws ParseException {
		BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
		
		String title = queryObj.getQueryTitle();
		String description = queryObj.getQueryDescription();
		
		if (!title.isEmpty()) {
			/*
			 * If the title is in the form of "Ireland, peace talks",
			 * then build the query as
			 * "Ireland" AND "peace talks"
			 */
			if (title.contains(",")) {
				String[] titleTokens = title.split(",");
				for (String token : titleTokens) {
					Query tokenQuery = this.queryParser.parse(QueryParser.escape(token));
					finalQuery.add(tokenQuery, BooleanClause.Occur.SHOULD);
				}
			} else {
				Query titleQuery = this.queryParser.parse(QueryParser.escape(title));
				finalQuery.add(titleQuery, BooleanClause.Occur.SHOULD);
			}
		}
		
		if (!description.isEmpty()) {
			try {
				Query descriptionQuery = this.queryParser.parse(QueryParser.escape(stringFilter(description)));
				finalQuery.add(descriptionQuery, BooleanClause.Occur.SHOULD);	
			} catch (Exception e) {
				LOGGER.severe("Unable to use description: " + stringFilter(description));
				e.printStackTrace();
			}
			
		}
		
		return finalQuery.build();
	}
	
	private String stringFilter(String str) {
		str = str.toLowerCase().replace("\"", "").trim().replace("?", "");
		
		return str.replaceAll("documents", "")
			      .replaceAll("document", "")
			      .replaceAll("relevant", "")
			      .replaceAll("include", "")
			      .replaceAll("discussions", "")
			      .replaceAll("discussion", "")
			      .replaceAll("discuss", "")
			      .replaceAll("results", "")
			      .replaceAll("result", "")
			      .replaceAll("describing", "")
			      .replaceAll("described", "")
			      .replaceAll("describe", "")
			      .replaceAll("provides", "")
			      .replaceAll("provide", "")
			      .replaceAll("find", "")
			      .replaceAll("information", " ");
	}
	
	private Map<String, Float> buildBoostMap() {
		Map<String, Float> boost = new HashMap<>();
        boost.put("headline", (float) 1.0);
        boost.put("text", (float) 0.5);
        return boost;
	}
	
	protected void finalize() {
		try {
			reader.close();
		} catch (IOException e) {
			LOGGER.severe("Unable to destroy QueryEngine");
			LOGGER.severe(e.getMessage());
		}
	}
}
