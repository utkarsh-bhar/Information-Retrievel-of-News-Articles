package ie.tcd.lucene.scobo.parsers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.IntPoint;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class FR94Parser {
	private static List<Document> frDocList = new ArrayList<>();

    public static List<Document> loadFrDocs(String pathFR) throws IOException {
    	File[] dirs = new File(pathFR).listFiles(File::isDirectory);
		String docno, text,title;
		for(File dir : dirs) {
			File[] files = dir.listFiles();
			for(File file : files) {
				org.jsoup.nodes.Document d =  Jsoup.parse(file,null,"");
				Elements docs = d.select("DOC");
				for(Element doc : docs) {
					title = doc.select("DOCTITLE").text();

                    doc.select("DOCTITLE").remove();
                    doc.select("ADDRESS").remove();
                    doc.select("SIGNER").remove();
                    doc.select("SIGNJOB").remove();
                    doc.select("BILLING").remove();
                    doc.select("FRFILING").remove();
                    doc.select("DATE").remove();
                    doc.select("CRFNO").remove();
                    doc.select("RINDOCK").remove();

                    docno = doc.select("DOCNO").text();
                    text = doc.select("TEXT").text();

                    addFedRegisterDoc(docno, text, title);

				}
				
			}
		}
		return frDocList;
    }

    private static void addFedRegisterDoc(String docno, String text, String title) {
        Document doc = new Document();
        doc.add(new TextField("docno", docno, Field.Store.YES));
        doc.add(new TextField("text", text, Field.Store.YES));
        doc.add(new TextField("headline", title, Field.Store.YES));
        frDocList.add(doc);
    }
}

