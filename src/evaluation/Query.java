package evaluation;

import java.util.Map;

public class Query {
	private String id;
	private String text;
	// Table des documents pertinents pour la requï¿½te
	private Map<String, Double>relevants;
	// Contient pour un doc son cluster associé
	private Map<String, String> clusters; 

	public Query(String id, String text, Map<String, Double> relevants) {
		super();
		this.id = id;
		this.text = text;
		this.relevants = relevants;
		this.clusters = null;
	}
	
	public Query(String id, String text, Map<String, Double> relevants, Map<String, String> clusters) {
		super();
		this.id = id;
		this.text = text;
		this.relevants = relevants;
		this.clusters = clusters;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public Map<String, Double> getRelevants() {
		return relevants;
	}
	
	public Map<String, String> getClusters() {
		return clusters;
	}
	
}
