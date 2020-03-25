package ie.tcd.lucene.scobo.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ie.tcd.lucene.scobo.models.LATimesModel;

public class LATimesParser {
	private static Logger LOGGER = Logger.getLogger(FinancialTimesLtdParser.class.getName());

	protected static final String DOC = "DOC";
	protected static final String DOC_NO = "DOCNO";
	protected static final String DOC_ID = "DOCID";
	protected static final String TEXT = "TEXT";
	protected static final String HEADLINE = "HEADLINE";
	
	public static List<Document> loadDocuments(List<String> fileNames) throws IOException {
		List<Document> documentsList = new ArrayList<>();
		File file;
		
		LOGGER.info("Parsing LATimes Docs");
		
		try {
			for (String fileName : fileNames) {
				file = new File(fileName);
				org.jsoup.nodes.Document htmlDocument = Jsoup.parse(file, null, "");
				
				Elements docElements = htmlDocument.select(DOC);
	
				for (Element htmlDoc : docElements) {
					Document doc = createNewDocument(htmlDoc);
					documentsList.add(doc);
				}
			}
		} catch(Exception e) {
			LOGGER.severe(e.getMessage());
		}
		
		LOGGER.info(String.format("Parsed %d LATimes Docs", documentsList.size()));
		
		return documentsList;
	}
	
	private static Document createNewDocument(Element htmlDoc) {
		
		Document doc = new Document();
		doc.add(new StringField("doc_id", htmlDoc.select(DOC_ID).text(), Field.Store.YES));
		doc.add(new StringField("doc_no", htmlDoc.select(DOC_NO).text(), Field.Store.YES));
        doc.add(new TextField("headline", htmlDoc.select(HEADLINE).select("P").text(), Field.Store.YES));
        doc.add(new TextField("text", htmlDoc.select(TEXT).select("P").text(), Field.Store.YES));
        
        return doc;
	}
}
