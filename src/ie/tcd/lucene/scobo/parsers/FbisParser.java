package ie.tcd.lucene.scobo.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FbisParser {
	
	private static Logger LOGGER = Logger.getLogger(LATimesParser.class.getName());

	protected static final String DOC = "DOC";
	protected static final String DOC_NO = "DOCNO";
	protected static final String DOC_ID = "DOCID";
	protected static final String TEXT = "TEXT";
	protected static final String TITLE = "T1";
	
	public static List<Document> loadDocuments(List<String> fileNames) throws IOException {
		List<Document> documentsList = new ArrayList<>();
		File file;
		
		LOGGER.info("Parsing FBIS Docs");
		
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
		
		LOGGER.info(String.format("Parsed %d FBIS Docs", documentsList.size()));
		
		return documentsList;
	}
	
	private static Document createNewDocument(Element htmlDoc) {
		
		Document doc = new Document();
		doc.add(new StringField("doc_id", htmlDoc.select(DOC_ID).text(), Field.Store.YES));
		doc.add(new StringField("doc_no", htmlDoc.select(DOC_NO).text(), Field.Store.YES));
        doc.add(new TextField("title", htmlDoc.select(TITLE).text(), Field.Store.YES));
        
        String textContent = htmlDoc.select(TEXT).text();
		if (!textContent.contains("[Text]")) {
            if (textContent.contains("[Passage omitted]")) {
                textContent = textContent.substring(textContent.indexOf("[Passage omitted]")).replace("[Passage omitted]", "").trim();
            } else if (textContent.contains("[Excerpts]")) {
                textContent = textContent.substring(textContent.indexOf("[Excerpts]")).replace("[Excerpts]", "").trim();
            }
        } else {
            textContent = textContent.substring(textContent.indexOf("[Text]")).replace("[Text]", "").trim();
        }
        
        doc.add(new TextField("text", textContent, Field.Store.YES));
        
        return doc;
	}
}
