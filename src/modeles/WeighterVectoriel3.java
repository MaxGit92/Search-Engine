package modeles;

import indexation.Index;

import java.io.RandomAccessFile;
import java.util.HashMap;

public class WeighterVectoriel3 extends Weighter {

	public WeighterVectoriel3(String indexName, String invertedName, Index indexObjet) {
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
		int nbDocs = this.indexObjet.getDocFrom().getId().size();
		for(String key : query.keySet()){
			HashMap<String,Double> poids=indexObjet.getTfsForStem(key, this.inverted);
			if(poids==null || poids.size()==0){
				continue;
			}
			double idf=Math.log(nbDocs/poids.size());
			res.put(key,idf);
		}
		return res;
	}
	
	@Override
	public String toString(){
		return "Weighter vectoriel 3";
	}
	
}
