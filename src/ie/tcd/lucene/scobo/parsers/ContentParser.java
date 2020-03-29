package ie.tcd.lucene.scobo.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;

public class ContentParser {
	private static Logger LOGGER = Logger.getLogger(ContentParser.class.getName());

	private String sourceDir;
	
	private List<Document> financialTimesLtdDocs;
	private List<Document> fedRegisterDocs;
	private List<Document> laTimesDocs;
	private List<Document> fbisDocs;
	
	/*
	 * Getters 
	 */
	
	public List<Document> getFinancialTimesLtdDocs() {
		return financialTimesLtdDocs;
	}


	public List<Document> getFedRegisterDocs() {
		return fedRegisterDocs;
	}


	public List<Document> getLaTimesDocs() {
		return laTimesDocs;
	}


	public List<Document> getFbisDocs() {
		return fbisDocs;
	}


	public ContentParser(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	/*
	 * Load all newspaper articles
	 */
	public void loadContentFiles() throws IOException {
		LOGGER.info("Loading documents");
		
		Path docDir = Paths.get(this.sourceDir + "/latimes");
		List<String> laTimesFiles = getFileNamesFromDirTree(docDir);
		new LATimesParser();
		this.laTimesDocs = LATimesParser.loadDocuments(laTimesFiles);
		
		docDir = Paths.get(this.sourceDir + "/ft");
		List<String> financialTimesLtdFiles = getFileNamesFromDirTree(docDir);
		new FinancialTimesLtdParser();
		this.financialTimesLtdDocs = FinancialTimesLtdParser.loadDocuments(financialTimesLtdFiles);
		
		docDir = Paths.get(this.sourceDir + "/fbis");
		List<String> fbisFiles = getFileNamesFromDirTree(docDir);
		new FbisParser();
		this.fbisDocs = FbisParser.loadDocuments(fbisFiles);
		
		docDir = Paths.get(this.sourceDir + "/fr94");
		List<String> frFiles = getFileNamesFromDirTree(docDir);
		new FR94Parser();
		this.fbisDocs = FR94Parser.loadDocuments(frFiles);
	}
	
	private static List<String> getFileNamesFromDirTree(Path rootDir){
        List<String> filesList = new ArrayList<>();
        try {
        	Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
					File file = new File(filePath.toString());
					if(file.isFile()){
					    filesList.add(filePath.toString());
					}
					return FileVisitResult.CONTINUE;
				}
			});
        } catch (IOException e) {
            LOGGER.severe("Error while walking through directiory " + rootDir);
            LOGGER.severe(e.getMessage());
        }
        
        return filesList;
    }
}
