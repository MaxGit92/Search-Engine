package modeles;

import indexation.Index;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Weighter {
	
	protected RandomAccessFile index;
	protected String indexName;
	protected RandomAccessFile inverted;
	protected String invertedName;
	protected Index indexObjet;
	
	public Weighter(String indexName, String invertedName, Index indexObjet) {
		super();
		this.indexName = indexName;
		this.invertedName = invertedName;
		try {
			this.index = new RandomAccessFile(indexName, "r");
		} catch (FileNotFoundException e) {
			this.index=null;
			e.printStackTrace();
		}
		try {
			this.inverted = new RandomAccessFile(invertedName, "r");
		} catch (FileNotFoundException e) {
			this.inverted=null;
			e.printStackTrace();
		}
		this.indexObjet = indexObjet;
	}
	
	/**
	 * Retourne les poids des
	termes pour un document dont l'identiant est passé en paramètre.
	 * @param idDoc, index
	 * @return HashMap<String,Double>
	 */
	public abstract HashMap<String,Double> getDocWeightsForDoc(String idDoc, RandomAccessFile index);
	/**
	 * Retourne les poids du
	terme stem dans tous les documents qui le contiennent.
	 * @param stem, inverted
	 * @return HashMap<String,Double>
	 */
	public abstract HashMap<String,Double> getDocWeightsForStem(String stem, RandomAccessFile inverted);
	/**
	 * Retourne les poids des termes pour la requête dont les tf sont passés en paramètres.
	 * @param query
	 * @return HashMap<String,Double>
	 */
	public abstract HashMap<String,Double> getWeightsForQuery(HashMap<String,Integer> query);
	
	public Index getIndexObjet(){
		return indexObjet;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getInvertedName() {
		return invertedName;
	}

}
