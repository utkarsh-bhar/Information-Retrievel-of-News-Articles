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

public class FbisParser {

	public class FileStructure {

	    String newsPaperName , documentTitle,aticleContent, documentDate,documentID;
	    public FileStructure(String newsPaperName, String documentTitle, String aticleContent, String documentDate, String documentID) {
	        this.newsPaperName = newsPaperName;
	        this.documentTitle = documentTitle;
	        this.aticleContent = aticleContent;
	        this.documentDate = documentDate;
	        this.documentID = documentID;
	    }
	}
	
	 public String readFile(File file) {


	        StringBuilder stringbuilder = new StringBuilder();
	        FileInputStream fstream = null;
	        try {
	            fstream = new FileInputStream(file);

	            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(fstream));

	            String strLine;
	            while ((strLine = bufferedreader.readLine()) != null) {

	            	stringbuilder.append(strLine);
	            }
	            bufferedreader.close();

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return stringbuilder.toString();
	    }
	 public ArrayList<FileStructure> parseFiles(String content){

         String[] docs = content.split("</DOC>");
         ArrayList<FileStructure> parsedList = new ArrayList<>();

         for (int i = 0; i < docs.length; i++) {

             try {

                 String document = docs[i];

                 String newsPaper = "Foreign Broadcast Information Service - FBIS";

                 if (!document.contains("<DOCNO>")) {
                     continue;
                 }

                 String docNumber = document.substring(document.indexOf("<DOCNO>"), document.indexOf("</DOCNO>")).replace("<DOCNO>", "").trim();
                 String date = document.substring(document.indexOf("<DATE1>"), document.indexOf("</DATE1>")).replace("<DATE1>", "").trim();
                 String title = document.substring(document.indexOf("<TI>"), document.indexOf("</TI>")).replace("<TI>", "").trim();
                 String textContent = document.substring(document.indexOf("<TEXT>"), document.indexOf("</TEXT>")).replace("<TEXT>", "").trim();
           // System.out.println(title);
            DateFormat df = new SimpleDateFormat("MMMM dd yyyy");
            DateFormat simpleformat = new SimpleDateFormat("MMMM dd, yyyy");

            try {
                date = simpleformat.format(df.parse(date));
            } catch (ParseException e) {
                DateFormat format1 = new SimpleDateFormat("dd MMM yyyy");
                try {
                    date = simpleformat.format(format1.parse(date));
                } catch (ParseException e1) {
                    DateFormat format2 = new SimpleDateFormat("dd MMMM");
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(format2.parse(date));
                        calendar.set(Calendar.YEAR, 1994);
                        date = simpleformat.format(calendar.getTime());
                    } catch (ParseException e2) {
//                        e2.printStackTrace();
                    }
                }
//            e.printStackTrace();
            }

            if (!textContent.contains("[Text]")) {

                if (textContent.contains("[Passage omitted]")) {
                    textContent = textContent.substring(textContent.indexOf("[Passage omitted]")).replace("[Passage omitted]", "").trim();
                } else if (textContent.contains("[Excerpts]")) {
                    textContent = textContent.substring(textContent.indexOf("[Excerpts]")).replace("[Excerpts]", "").trim();
                } else {
                //System.out.println(textContent);
                break;
                }
            } else {
                textContent = textContent.substring(textContent.indexOf("[Text]")).replace("[Text]", "").trim();
            }
            System.out.println(docNumber);
            System.out.println(title);
            System.out.println(date);
            
            FileStructure newFileStructure = new FileStructure(newsPaper, title, textContent, date, docNumber);
            parsedList.add(newFileStructure);

        } catch (Exception e) {
            e.printStackTrace();
            continue;
        }

    }
    return parsedList;

}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 String docPath = "C:\\Users\\GITHU SAVY\\Documents\\InformationRetrievalAndWebSearch\\Assignment Two\\fbis";
		  ArrayList<FileStructure> totalContent = new ArrayList<>();
	      File doc = new File(docPath);
	        FbisParser fbis=new FbisParser();
	        System.out.println("Reading Foreign Broadcast Information Service...");
	        if (doc.exists() && doc.isDirectory()) {
	            File[] files = doc.listFiles();
	            for (int i = 0; i < files.length; i++) {
	                if (files[i].isFile() && !files[i].getName().startsWith("read")) {
	                    String content = fbis.readFile(files[i]);
                  //      System.out.println(content);
	                    
	                    ArrayList<FileStructure> singleContent = fbis.parseFiles(content);
	                   totalContent.addAll(singleContent);

	                }
	            }
	        }

	       System.out.println("Total Number of Documents "+totalContent.size());

	}

}
