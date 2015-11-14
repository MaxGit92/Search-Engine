package evaluation;

import java.util.Map;

public class IRList {
	public Query query; // la requ�te
	public Map<String, Double> documents;//liste ordonn�e de documents en fonction de leur pertinence
	
	public IRList(Query query, Map<String, Double> documents) {
		super();
		this.query = query;
		this.documents = documents;
	}

	public Query getQuery() {
		return query;
	}

	public Map<String, Double> getDocuments() {
		return documents;
	}
	
	
}
