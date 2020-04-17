package ie.tcd.lucene.scobo;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.apache.lucene.search.BoostQuery;
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
		
		Query narrativeQuery = null;
		List<String> splitNarrative = splitNarrativeIntoRelNotRel(queryObj.getQueryNarrative());
		String relevantNarrative = splitNarrative.get(0).trim();

		QueryParser parserForText = new QueryParser("text", analyzer);

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
					finalQuery.add(new BoostQuery(tokenQuery,(float) 25.0), BooleanClause.Occur.SHOULD);
				}
			} else {
				Query titleQuery = this.queryParser.parse(QueryParser.escape(title));
				finalQuery.add(new BoostQuery(titleQuery,(float) 23.0), BooleanClause.Occur.SHOULD);
			}
		}
		
		if (!description.isEmpty()) {
			description = stringFilter(description);

			try {
				Query descriptionQuery = parserForText.parse(QueryParser.escape(description));
				finalQuery.add(new BoostQuery(descriptionQuery,(float) 20.0), BooleanClause.Occur.SHOULD);	
			} catch (ArrayIndexOutOfBoundsException e) {
				LOGGER.severe("Unable to use description: " + description);
			}
			
		}
		if(!relevantNarrative.isEmpty()){
			try {
				narrativeQuery = parserForText.parse(QueryParser.escape(stringFilter(relevantNarrative)));
				if (narrativeQuery != null) {
					finalQuery.add(new BoostQuery(narrativeQuery,(float)15.0),BooleanClause.Occur.SHOULD);
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				LOGGER.severe("Unable to use narrative: " + stringFilter(relevantNarrative));
			}
		}
		
		return finalQuery.build();
	}
	
	
	
	private List<String> splitNarrativeIntoRelNotRel(String queryNarrative) {
        StringBuilder relevantNarrative = new StringBuilder();
        StringBuilder irrelevantNarrative = new StringBuilder();
        List<String> splitNarrative = new ArrayList<>();

        BreakIterator breakIterator = BreakIterator.getSentenceInstance();
        breakIterator.setText(queryNarrative);
        int index = 0;
        while (breakIterator.next() != BreakIterator.DONE) {
            String sentence = queryNarrative.substring(index, breakIterator.current());

            if (!sentence.contains("not relevant") && !sentence.contains("irrelevant")) {
                relevantNarrative.append(sentence.replaceAll(
                        "a relevant document identifies|a relevant document could|a relevant document may|a relevant document must|a relevant document will|a document will|to be relevant|relevant documents|a document must|relevant|will contain|will discuss|will provide|must cite",
                        ""));
            } else {
                irrelevantNarrative.append(sentence.replaceAll("are also not relevant|are not relevant|are irrelevant|is not relevant|not|NOT", ""));
            }
            index = breakIterator.current();
        }
        splitNarrative.add(relevantNarrative.toString());
        splitNarrative.add(irrelevantNarrative.toString());
        return splitNarrative;
	}
	
	private String stringFilter(String str) {
		str = str.toLowerCase();
		// Some of these should really be regex
		
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
			      .replaceAll("information", "")
			      .replaceAll("\"", "")
			      .replaceAll("u.s.", "US")
			      .replaceAll("i.e.,", "")
			      .replaceAll("\\(", "")
			      .replaceAll("\\)", "")
			      .replace("?", "")
			      .replace(",", "")
			      .replace(".",  "").trim();
	}
	
	private Map<String, Float> buildBoostMap() {
		Map<String, Float> boost = new HashMap<>();
        boost.put("headline", (float) 0.6);
        boost.put("text", (float) 1.57);
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
