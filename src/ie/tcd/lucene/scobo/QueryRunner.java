package ie.tcd.lucene.scobo;

import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ie.tcd.lucene.scobo.analyzers.AnalyzerHelper;
import ie.tcd.lucene.scobo.parsers.QueryDocs.QueryObjects;
import ie.tcd.lucene.scobo.parsers.QueryDocs.QueryLoader;

public class QueryRunner {
	private static Logger LOGGER = Logger.getLogger(Indexer.class.getName());
	private static Integer paginationCount = 1000;
	
	public static void main(String[] args) throws Exception {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		LOGGER.info("Current relative path is: " + s);

		String indexPath = null;
		String queryFilePath = null;
		String outputFilePath = null;
		String analyzerType = null;
		String scoring = null;
		
		// Handle arguments
		for (int i = 0; i < args.length; i++) {
			if ("-scoring".equals(args[i])) {
				scoring = args[i + 1];
				i++;
			} else if ("-analyzer".equals(args[i])) {
				analyzerType = args[i + 1];
				i++;
			} else if ("-index".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					indexPath = args[i + 1];
				else
					throw new RuntimeException("Must index path.");
				i++;
			} else if ("-queries".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					queryFilePath = args[i + 1];
				else
					throw new RuntimeException("Must specify queries file");
				i++;
			} else if ("-output".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					outputFilePath = args[i + 1];
				else
					throw new RuntimeException("Must specify output file");
				i++;
			}
		}
		
		Analyzer indexAnalyzer = AnalyzerHelper.getAnalyzer(analyzerType); 
		Similarity similarityScorer = AnalyzerHelper.getSimilarity(scoring);
		Directory indexDir = FSDirectory.open(Paths.get(indexPath));
	
		// Load queries
		new QueryLoader();
		List<QueryObjects> queries = QueryLoader.loadQueries(queryFilePath);
		
		// Initialize query engine
		QueryEngine engine = new QueryEngine(indexDir, indexAnalyzer, similarityScorer);
		IndexSearcher indexSearcher = engine.getSearcher();
		FileWriter csvWriter = new FileWriter(outputFilePath);
		
		int queryCount = 1;
		for (QueryObjects queryObj : queries) {
			BooleanQuery query = engine.buildQuery(queryObj);
			ScoreDoc[] hits = engine.executeQuery(query, paginationCount);
			
			// Load each hit as a queryResult
			for (int i = 0; i < hits.length; ++i) {
				String docNo = indexSearcher.doc(hits[i].doc).get("doc_no");
				double score = hits[i].score;
				
				csvWriter.append(queryCount + " " + "0" + " " + (docNo) + " " + (i + 1) + " " + score + " END" );
				csvWriter.append("\n");
			}

			queryCount++;
		}
		
		// Cleanup
		csvWriter.flush();
		csvWriter.close();
		
		LOGGER.info("Completed!");
		LOGGER.info(String.format("Total number of queries executed: %d", queryCount));
		
		
	}
}
