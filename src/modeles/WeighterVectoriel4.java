package modeles;

import java.io.RandomAccessFile;
import java.util.HashMap;

import indexation.Index;

public class WeighterVectoriel4 extends Weighter{
	public WeighterVectoriel4(String indexName, String invertedName, Index indexObjet) {
		super(indexName, invertedName, indexObjet);
	}

	@Override
	public HashMap<String,Double> getDocWeightsForDoc(String idDoc, RandomAccessFile index) { // pour un document retourner tous ces poids (index normal)
		HashMap<String,Double> wtd = this.indexObjet.getTfsForDoc(idDoc, index);
		for(String stem : wtd.keySet()){
			if(wtd.get(stem) == 0) continue;
			wtd.put(stem, 1+Math.log(wtd.get(stem)));
		}
		return wtd;
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
		return "Weighter vectoriel 4";
	}
	
}
