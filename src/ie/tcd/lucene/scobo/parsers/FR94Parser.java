package ie.tcd.lucene.scobo.parsers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FR94Parser {
	private static Logger LOGGER = Logger.getLogger(FR94Parser.class.getName());

	public static List<Document> loadDocuments(List<String> fileNames) throws IOException {
		List<Document> documentsList = new ArrayList<>();
		File file;
		
		LOGGER.info("Parsing FR94 Docs");
		
		try {
			for (String fileName : fileNames) {
				file = new File(fileName);
				org.jsoup.nodes.Document htmlDocument = Jsoup.parse(file, null, "");
				Elements docElements = htmlDocument.select("DOC");
				for (Element htmlDoc : docElements) {
					
					String title = htmlDoc.select("DOCTITLE").text();
					
					// Remove as not required
                    htmlDoc.select("DOCTITLE").remove();
                    htmlDoc.select("ADDRESS").remove();
                    htmlDoc.select("SIGNER").remove();
                    htmlDoc.select("SIGNJOB").remove();
                    htmlDoc.select("BILLING").remove();
                    htmlDoc.select("FRFILING").remove();
                    htmlDoc.select("DATE").remove();
                    htmlDoc.select("CRFNO").remove();
                    htmlDoc.select("RINDOCK").remove();
                    
                    String docno = htmlDoc.select("DOCNO").text();
                    String text = htmlDoc.select("TEXT").text();
                    
                    Document doc = createDocument(docno, text, title);
                    documentsList.add(doc);
				}
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		
		LOGGER.info(String.format("Parsed %d FR94 Docs", documentsList.size()));
		
		return documentsList;
	}
	
    private static Document createDocument(String docno, String text, String title) {
        Document doc = new Document();
        doc.add(new StringField("doc_no", docno, Field.Store.YES));
        doc.add(new TextField("text", text, Field.Store.YES));
        doc.add(new TextField("headline", title, Field.Store.YES));
        
        return doc;
    }
}

