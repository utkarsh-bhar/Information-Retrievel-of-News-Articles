package ie.tcd.lucene.scobo.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class AnalyzerHelper {
	private AnalyzerHelper() { }
	
	public static Similarity getSimilarity(String scoring) {
		if ("bm25".equals(scoring)) {
			return new BM25Similarity(1.2f, 0.75f);
		} else if ("tfidf".equals(scoring)) {
			return new ClassicSimilarity();
		} else {
			System.out.println("No scorer provided. Defaulting to BM25");
			return new BM25Similarity(1.2f, 0.75f);
		}
	}
	
	public static Analyzer getAnalyzer(String analyzerType) {
		if ("englishAnalyzer".equals(analyzerType))
			return new EnglishAnalyzer();
		else if ("standardAnalyzer".equals(analyzerType)) {
			return new StandardAnalyzer();
		} else {
			System.out.println("No analyzer provided. Defaulting to English Analyzer.");
			return new EnglishAnalyzer();
		}
	}
}
