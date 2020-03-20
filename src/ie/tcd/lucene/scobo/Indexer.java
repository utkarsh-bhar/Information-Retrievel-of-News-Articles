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
	private static Logger logger = Logger.getLogger(Indexer.class.getName());
	
	public static void main(String[] args) throws Exception {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		logger.info("Current relative path is: " + s);

		String indexPath = null;
		String analyzerType = null;
		String sourceFile = null;
		String scoring = null;
		
		for (int i = 0; i < args.length; i++) {
			if ("-scoring".equals(args[i])) {
				scoring = args[i + 1];
				i++;
			} else if ("-analyzer".equals(args[i])) {
				analyzerType = args[i + 1];
				i++;
			} else if ("-document".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					sourceFile = args[i + 1];
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
		
		ContentParser contentParser = new ContentParser(sourceFile);
		contentParser.loadContentFile();
		List<Document> docs = contentParser.getDocuments();
		
		logger.info("Loaded Content");
		
		Analyzer indexAnalyzer = AnalyzerHelper.getAnalyzer(analyzerType); 
		Similarity similarityScorer = AnalyzerHelper.getSimilarity(scoring);
		
		IndexWriterConfig config = createIndexWriterConfig(indexAnalyzer, IndexWriterConfig.OpenMode.CREATE, similarityScorer);
		Directory indexDir = FSDirectory.open(Paths.get(indexPath));
		
		// Load indexes to path
		try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
			indexWriter.addDocuments(docs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Successfully indexed (" + docs.size() + ") documents.");
	}
	
	private static IndexWriterConfig createIndexWriterConfig(Analyzer analyzer, IndexWriterConfig.OpenMode openMode, Similarity simiarityScorer) throws Exception {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(openMode);
		config.setSimilarity(simiarityScorer);
		return config;
	}
}
