package ie.tcd.lucene.scobo.analyzers;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.AfterEffectB;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicModelIn;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.NormalizationH1;
import org.apache.lucene.search.similarities.Similarity;

public class AnalyzerHelper {
	private AnalyzerHelper() { }
	
	public static Similarity getSimilarity(String scoring) {
		if ("bm25".equals(scoring)) {
			return new BM25Similarity(1.2f, 0.75f);
		} else if ("tfidf".equals(scoring)) {
			return new ClassicSimilarity();
		}else if ("multisimilarity".equals(scoring)) {
			return  multiSimilarity();
		} else {
			System.out.println("No scorer provided. Defaulting to BM25");
			return new BM25Similarity(1.2f, 0.75f);
		}
	}
	
	public static Analyzer getAnalyzer(String analyzerType) throws IOException {
		if ("englishAnalyzer".equals(analyzerType))
			return new EnglishAnalyzer();
		else if ("standardAnalyzer".equals(analyzerType)) {
			return new StandardAnalyzer();
		}
		else if ("customAnalyzer".equals(analyzerType)) {
			return  customAnalyzer();
		}else {
			System.out.println("No analyzer provided. Defaulting to English Analyzer.");
			return new EnglishAnalyzer();
		}
	}
	public static Analyzer customAnalyzer() throws IOException {
		Analyzer analyzer = CustomAnalyzer.builder()
			      .withTokenizer("standard")
			      .addTokenFilter("lowercase")
			      .addTokenFilter("stop")
			      .addTokenFilter("porterstem")
			      .addTokenFilter("capitalization")
			      .build();
		return analyzer;
	}
public static Similarity multiSimilarity() {
	Similarity similarity[] = { new BM25Similarity(3, (float) 0.9),
			new DFRSimilarity(new BasicModelIn(), new AfterEffectB(), new NormalizationH1()),
			new LMDirichletSimilarity(5000) };
	return (new MultiSimilarity(similarity));
}
}
