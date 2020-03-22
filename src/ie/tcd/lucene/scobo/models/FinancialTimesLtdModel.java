package ie.tcd.lucene.scobo.models;

public class FinancialTimesLtdModel {
	private String docNo;
    private String docId;
    private String headline;
    private String byLine;
    private String text;
    private String date;
    
    public FinancialTimesLtdModel() {
        this.docNo = "";
        this.docId = "";
        this.headline = "";
        this.byLine = "";
        this.text = "";
        this.date = "";
    }
    
    public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getByLine() {
		return byLine;
	}

	public void setByLine(String byLine) {
		this.byLine = byLine;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
