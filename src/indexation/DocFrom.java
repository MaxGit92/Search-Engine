package indexation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/*
 * Classe spécifique à sauvegarder qui contient quasiment toutes
 * les informations nécessaire sur l'indexation des documents
 */
public class DocFrom implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> fileSource;
	private List<String> id;
	private Map<String, Long> pos;
	private Map<String, Long> longueur;
	private Map<String,Long> posInIndex;
	private Map<String,Long> nbMots;
	private Map<String, HashSet<String>> linksDoc;
	
	public DocFrom(Map<String, String> fileSource, List<String> id,
			Map<String, Long> posInIndex, Map<String, Long> pos,
			Map<String, Long> longueur, Map<String,Long> nbMots,
			Map<String, HashSet<String>> linksDoc) {
		super();
		this.fileSource = fileSource;
		this.id = id;
		this.pos = pos;
		this.longueur = longueur;
		this.posInIndex = posInIndex;
		this.nbMots = nbMots;
		this.linksDoc = linksDoc;
	}
	public DocFrom(){
		this.fileSource = new HashMap<String, String>();
		this.id = new ArrayList<String>();
		this.posInIndex = new HashMap<String, Long>();
		this.pos = new HashMap<String, Long>();
		this.longueur = new HashMap<String, Long>();
		this.nbMots = new HashMap<String,Long>();
		this.linksDoc = new HashMap<String, HashSet<String>>();
	}
	public Map<String, String> getFileSource() {
		return fileSource;
	}
	public List<String> getId() {
		return id;
	}
	public Map<String, Long> getPosInIndex() {
		return posInIndex;
	}
	public Map<String, Long> getLongueur() {
		return longueur;
	}
	public Map<String, Long> getPos() {
		return pos;
	}
	public Map<String, Long> getNbMots() {
		return nbMots;
	}
	public Map<String, HashSet<String>> getLinksDoc() {
		return linksDoc;
	}
	
}
