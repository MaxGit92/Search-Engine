package modeles;

import indexation.Index;

import java.io.RandomAccessFile;
import java.util.HashMap;

public class WeighterVectoriel1 extends Weighter{
	
	public WeighterVectoriel1(String indexName, String invertedName, Index indexObjet) {
		super(indexName, invertedName, indexObjet);
	}

	@Override
	public HashMap<String,Double> getDocWeightsForDoc(String idDoc, RandomAccessFile index) { // pour un document retourner tous ces poids (index normal)
		return this.indexObjet.getTfsForDoc(idDoc, index);
	}

	@Override
	public HashMap<String,Double> getDocWeightsForStem(String stem, RandomAccessFile inverted) { // pour un stem retourner les poids dans chaque document (index inversé)
		return this.indexObjet.getTfsForStem(stem, inverted);
	}
	
	@Override
	public HashMap<String,Double> getWeightsForQuery(HashMap<String, Integer> query) {
		HashMap<String,Double> res = new HashMap<String, Double>();
		for(String key : query.keySet()){
			res.put(key, (double)1);
		}
		return res;
	}
	
	@Override
	public String toString(){
		return "Weighter vectoriel 1";
	}
}
