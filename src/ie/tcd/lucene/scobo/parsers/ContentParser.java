package ie.tcd.lucene.scobo.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class ContentParser {
	private static Logger logger = Logger.getLogger(ContentParser.class.getName());
	private String fileName;
	private List<Document> documents;
	private Integer corpusCount = 0;
	
	public ContentParser(String fileName) {
		this.fileName = fileName;
		this.documents = new ArrayList<>();
	}
	
	/*
	 * Getter methods
	 */
	
	public String getFileName() {
		return fileName;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public Integer getCorpusCount() {
		return corpusCount;
	}
	
	public void loadContentFile() {
		String docId = "";
		StringBuilder textAbstract = new StringBuilder();
		String authors = "";
		String bibliography = "";
		StringBuilder content = new StringBuilder();
		
		// Start of query marker
		String initialMarker = ".I";
		
		File file = new File(fileName);
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			
			// Till all lines are read
			while((line = br.readLine()) != null) {
				String lineMarker = ParserUtils.getLineMarker(line);
				
				if (lineMarker != null) {
					// If marker found as not .I, go to next line
					initialMarker = lineMarker;

					if(!".I".equals(lineMarker))
						line = br.readLine();
				}
				
				if (".I".equals(lineMarker)) {
					// Get document Id
					docId = line.split(" ")[1];
					corpusCount++;
					
					// Create the new document
					if (corpusCount > 1) {
						Document doc = createDocument(
											docId, 
											textAbstract.toString(), 
											authors, 
											bibliography, 
											content.toString());
						documents.add(doc);
					}
					
					// reset the previous variables
					docId = "";
					textAbstract = new StringBuilder();
					authors = "";
					bibliography = "";
					content = new StringBuilder();
					
				} else if (".T".equals(lineMarker)) {
					textAbstract.append(line);
				} else if (".A".equals(lineMarker)) {
					authors += line;
				} else if (".B".equals(lineMarker)) {
					bibliography += line;
				} else if (".W".equals(lineMarker)) {
					content.append(line);
				}
			}
			
			// Add final doc
			Document doc = createDocument(
								docId, 
								textAbstract.toString(), 
								authors, 
								bibliography, 
								content.toString());
			documents.add(doc);
			
			logger.info("Corpus count : " + corpusCount);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	/*
	 * Create new search document for lucene
	 */
	private Document createDocument(String docId, String title, String authors, String bibliography, String content) throws IOException {

		Document doc = new Document();
		
		doc.add(new StringField("docId", docId, Field.Store.YES));
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("author", authors, Field.Store.YES));
		doc.add(new StringField("bibliography", bibliography, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		
		return doc;
	}
}
