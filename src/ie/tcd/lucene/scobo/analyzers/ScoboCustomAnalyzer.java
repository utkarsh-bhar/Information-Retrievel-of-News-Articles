package ie.tcd.lucene.scobo.analyzers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;

import ie.tcd.lucene.scobo.Indexer;

public class ScoboCustomAnalyzer extends StopwordAnalyzerBase {
	private static Logger LOGGER = Logger.getLogger(ScoboCustomAnalyzer.class.getName());
	
	public ScoboCustomAnalyzer() {
		// Set stop words
		super(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
	}
	
	@Override
	protected TokenStreamComponents createComponents(String s) {
		final Tokenizer tokenizer = new StandardTokenizer();
		TokenStream tokenStream = new ClassicFilter(tokenizer);
		tokenStream = new LowerCaseFilter(tokenStream);
		tokenStream = new TrimFilter(tokenStream);
		tokenStream = new FlattenGraphFilter(
			new WordDelimiterGraphFilter(
				tokenStream, 
				WordDelimiterGraphFilter.SPLIT_ON_NUMERICS | 
				WordDelimiterGraphFilter.GENERATE_WORD_PARTS | 
				WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS | 
				WordDelimiterGraphFilter.PRESERVE_ORIGINAL, 
				null
			)
		);
		tokenStream = new FlattenGraphFilter(new SynonymGraphFilter(tokenStream, buildSynonymMap(), true));
		tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(Constants.STOP_WORDS_LIST, true));
		tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());
		return new TokenStreamComponents(tokenizer, tokenStream);
	}

	private SynonymMap buildSynonymMap() {
		SynonymMap map = new SynonymMap(null, null, 0);
		try {
            List<String> countriesList = getCountriesList();
			final SynonymMap.Builder builder = new SynonymMap.Builder(true);
			
			for (String country: countriesList) {
				builder.add(new CharsRef("country"), new CharsRef(country), true);
				builder.add(new CharsRef("countries"), new CharsRef(country), true);
			}

			map = builder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private List<String> getCountriesList() {
		List<String> countriesList = new ArrayList<String>();
		InputStream is = getContextClassLoader().getResourceAsStream("countries");
		InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);

		try (BufferedReader br = new BufferedReader(streamReader)) {
			String line;
			while((line = br.readLine()) != null) {
				countriesList.add(line);
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
		}
		return countriesList;
	}
	
	private ClassLoader getContextClassLoader() {
	    return Thread.currentThread().getContextClassLoader();
	}
}
