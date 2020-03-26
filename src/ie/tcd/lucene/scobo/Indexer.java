package ie.tcd.lucene.scobo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ie.tcd.lucene.scobo.analyzers.AnalyzerHelper;
import ie.tcd.lucene.scobo.parsers.ContentParser;

public class Indexer {
	private static Logger LOGGER = Logger.getLogger(Indexer.class.getName());
	
	public static void main(String[] args) throws Exception {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		LOGGER.info("Current relative path is: " + s);

		String indexPath = null;
		String analyzerType = null;
		String sourceDir = null;
		String scoring = null;
		
		for (int i = 0; i < args.length; i++) {
			if ("-scoring".equals(args[i])) {
				scoring = args[i + 1];
				i++;
			} else if ("-analyzer".equals(args[i])) {
				analyzerType = args[i + 1];
				i++;
			} else if ("-documents".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					sourceDir = args[i + 1];
				else
					throw new RuntimeException("Must specify CRAN source file.");
				i++;
			} else if ("-index-output".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					indexPath = args[i + 1];
				else
					throw new RuntimeException("Must specify index output");
				i++;
			}
		}

		// Load all documents
		
		ContentParser contentParser = new ContentParser(sourceDir);
		
		contentParser.loadContentFiles();
		
		List<Document> laTimesDocs = contentParser.getLaTimesDocs();
		List<Document> financialTimesLtdDocs = contentParser.getFinancialTimesLtdDocs();
		List<Document> fbisDocs = contentParser.getFbisDocs();
		
		LOGGER.info("Loaded all documents");
		
		
		// Configure analyzer, scorer, and index config
		
		Analyzer indexAnalyzer = AnalyzerHelper.getAnalyzer(analyzerType); 
		Similarity similarityScorer = AnalyzerHelper.getSimilarity(scoring);
		
		IndexWriterConfig config = createIndexWriterConfig(indexAnalyzer, IndexWriterConfig.OpenMode.CREATE, similarityScorer);
		Directory indexDir = FSDirectory.open(Paths.get(indexPath));
		
		
		// Load indexes to path
		
		LOGGER.info("Started Indexing...");
		
		try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
			indexWriter.addDocuments(financialTimesLtdDocs);
			indexWriter.addDocuments(laTimesDocs);
			indexWriter.addDocuments(fbisDocs);
			
			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LOGGER.info("Indexing Complete");
	}
	
	private static IndexWriterConfig createIndexWriterConfig(Analyzer analyzer, IndexWriterConfig.OpenMode openMode, Similarity simiarityScorer) throws Exception {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(openMode);
		config.setSimilarity(simiarityScorer);
		return config;
	}
}
