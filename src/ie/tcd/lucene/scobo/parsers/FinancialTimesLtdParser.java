package ie.tcd.lucene.scobo.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import ie.tcd.lucene.scobo.models.FinancialTimesLtdModel;

public class FinancialTimesLtdParser {
	private static Logger logger = Logger.getLogger(FinancialTimesLtdParser.class.getName());
	private static boolean headlineFlag, textFlag, byLineFlag = false;

	protected static final String DOC_START = "<DOC>";
	protected static final String DOC_END = "</DOC>";
	protected static final String DOC_NO_START = "<DOCNO>";
	protected static final String DOC_NO_END = "</DOCNO>";
	protected static final String DOC_ID_START = "<DOCID>";
	protected static final String DOC_ID_END = "</DOCID>";
	protected static final String TEXT_START = "<TEXT>";
	protected static final String TEXT_END = "</TEXT>";
	protected static final String HEADLINE_START = "<HEADLINE>"; 
	protected static final String HEADLINE_END = "</HEADLINE>";
	protected static final String BYLINE_START = "<BYLINE>"; 
	protected static final String BYLINE_END = "</BYLINE>";
	protected static final String DATE_START = "<DATE>";
	protected static final String DATE_END = "</DATE>";
	
	public static List<Document> loadDocuments(List<String> fileNames) throws IOException {
		FinancialTimesLtdModel model = new FinancialTimesLtdModel();
		List<Document> documentsList = new ArrayList<>();
		
		for (String fileName : fileNames) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				String line;
				
				while((line = br.readLine()) != null) {
					model = new FinancialTimesLtdModel();
					line = line.trim();
					model = buildModelFromLine(line, model);
				}
				
				Document doc = createNewDocument(model);
				documentsList.add(doc);
			} catch (FileNotFoundException e) {
				logger.warning(String.format("FileNotFoundException. File: %s \n %s", fileName, e.getMessage()));
			}
		}
		
		return documentsList;
	}
	
	private static FinancialTimesLtdModel buildModelFromLine(String line, FinancialTimesLtdModel model){
        if(line.contains(DOC_END)){
        	return model;
        } else if (line.contains(DOC_NO_START)){
        	model.setDocNo(getStringFromTags(line, "doc_no"));
        } else if (line.equals(HEADLINE_START)){
            headlineFlag = true;
        } else if(line.contains(HEADLINE_END)){
            headlineFlag = false;
        } else if (line.contains(BYLINE_START)){
            byLineFlag = true;
        } else if (line.contains(BYLINE_END)){
            byLineFlag = false;
        } else if (line.contains(TEXT_START)){
            textFlag = true;
        } else if (line.contains(TEXT_END)){
            textFlag = false;
        } else if (line.contains(DOC_ID_START)){
            model.setDocId(getStringFromTags(line, "doc_id"));
        }

        if(headlineFlag){
            model.setHeadline(model.getHeadline() + " " + getStringFromTags(line,
                    "headline"));
        } else if(textFlag){
            model.setText(model.getText() + " " + getStringFromTags(line, "text"));
        } else if(byLineFlag){
            model.setByLine(model.getByLine() + " " + getStringFromTags(line, "by_line"));
        }
        return model;
    }

	
	private static Document createNewDocument(FinancialTimesLtdModel model) {
		Document doc = new Document();
		
		doc.add(new StringField("doc_id", model.getDocId(), Field.Store.YES));
		doc.add(new StringField("doc_no", model.getDocNo(), Field.Store.YES));
        doc.add(new TextField("headline", model.getHeadline(), Field.Store.YES));
        doc.add(new StringField("by_line", model.getByLine(), Field.Store.YES));
        doc.add(new TextField("text", model.getText(), Field.Store.YES));
        
        return doc;
	}
	
	private static String getStringFromTags(String line, String field){
		String start_tag = "";
		String end_tag = "";
		
        switch (field){
            case "doc_id":
            	start_tag = TEXT_START;
            	end_tag = TEXT_END;
            	break;
            case "doc_no":
              	start_tag = DOC_NO_START;
            	end_tag = DOC_NO_END;
            	break;
            case "text":
              	start_tag = TEXT_START;
            	end_tag = TEXT_END;
            	break;
            case "by_line":
              	start_tag = BYLINE_START;
            	end_tag = BYLINE_END;
            	break;
            case "headline":
              	start_tag = HEADLINE_START;
            	end_tag = HEADLINE_END;
                break;
            default:
                start_tag = "";
                end_tag = "";
        }
        
        if (start_tag.isEmpty()) {
        	return null;
        }
        
        return line.replace(start_tag, "").replace(end_tag, "");
    }
}
