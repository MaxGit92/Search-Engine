package modeles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import indexation.Index;

public class WeighterVectoriel5 extends Weighter{
	public WeighterVectoriel5(String indexName, String invertedName, Index indexObjet) {
		super(indexName, invertedName, indexObjet);
	}

	@Override
	public HashMap<String,Double> getDocWeightsForDoc(String idDoc, RandomAccessFile index) { // pour un document retourner tous ces poids (index normal)
		HashMap<String,Double> wtd = this.indexObjet.getTfsForDoc(idDoc, index);
		int nbDocs = this.indexObjet.getDocFrom().getId().size();
		for(String stem : wtd.keySet()){
			if(wtd.get(stem) == 0) continue;
			try {
				RandomAccessFile r = new RandomAccessFile(invertedName, "r");
				HashMap<String,Double> poids=indexObjet.getTfsForStem(stem, r);
				r.close();
				wtd.put(stem, (1+Math.log(wtd.get(stem))*Math.log(nbDocs/poids.size())));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			res.put(key,(1+Math.log(query.get(key)))*idf);
		}
		return res;
	}
	
	@Override
	public String toString(){
		return "Weighter vectoriel 5";
	}
	
}
