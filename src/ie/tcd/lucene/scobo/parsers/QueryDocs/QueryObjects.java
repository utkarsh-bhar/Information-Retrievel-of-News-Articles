package ie.tcd.lucene.scobo.parsers.QueryDocs;

public class QueryObjects {
		private String queryNumber;
		private String qId;
		private String queryTitle;
		private String queryDescription;
		private String queryNarrative;
		
		
		QueryObjects(){
			this.queryNumber = "";
			this.qId = "";
			this.queryTitle = "";
			this.queryDescription = "";
			this.queryNarrative = "";
		}
		QueryObjects(String queryNumber , String qId, String queryTitle, String queryDescription, String queryNarrative){
			this.queryNumber = queryNumber;
			this.qId = qId;
			this.queryTitle = queryTitle;
			this.queryDescription = queryDescription;
			this.queryNarrative = queryNarrative;
		}
		
		public String getQueryNumber() {
			return queryNumber;
		}

		public void setQueryNumber(String queryNumber) {
			this.queryNumber = queryNumber;
		}

		public String getqId() {
			return qId;
		}

		public void setqId(String qId) {
			this.qId = qId;
		}

		public String getQueryTitle() {
			return queryTitle;
		}

		public void setQueryTitle(String queryTitle) {
			this.queryTitle = queryTitle;
		}

		public String getQueryDescription() {
			return queryDescription;
		}

		public void setQueryDescription(String queryDescription) {
			this.queryDescription = queryDescription;
		}

		public String getQueryNarrative() {
			return queryNarrative;
		}

		public void setQueryNarrative(String queryNarrative) {
			this.queryNarrative = queryNarrative;
		}

		
}
