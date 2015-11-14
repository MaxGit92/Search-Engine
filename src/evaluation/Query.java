package evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class Query {
	private String id;
	private String text;
	// Table des documents pertinents pour la requ�te
	private Map<String, Double>relevants;
	
	public Query(String id, String text, Map<String, Double> relevants) {
		super();
		this.id = id;
		this.text = text;
		this.relevants = relevants;
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
	
}
