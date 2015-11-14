package modeles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import evaluation.Query;
import features.Featurer;
import features.KeyDQ;
import indexation.Stemmer;
import indexation.TextRepresenter;

public class FeaturerModel extends Featurer{

	// Nous voulons sauvegarder uniquement l'attribut features
	private static final long serialVersionUID = -2412080409636085066L;
	private transient IRmodel irmodel;
	
	public FeaturerModel(RandomAccessFile index, String nomFichier, IRmodel irmodel) throws ClassNotFoundException, IOException {
		super(index, nomFichier);
		this.irmodel = irmodel;
	}

	@Override
	public List<Double> getFeatures(String idDoc, Query query) throws IOException, InterruptedException, ExecutionException {
		String[] s = {idDoc, query.getId()};
		if(!features.containsKey(s)){
			setListeFeatures(query);
			enregisterFeaturer();
		}
		return features.get(s);
	}
	
	/**
	 * FeatureModel fait appel à getScores de son attribut irmodel et met dans sa liste de features 
	 * les résultats obtenus pour chaque paIre de document avec la requete passée en paramètre
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void setListeFeatures(Query query) throws IOException, InterruptedException, ExecutionException{		
		TextRepresenter stemmer = new Stemmer();		
		HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());
		
		Map<String, Double> scores = this.irmodel.getScores(queryStem);
		for(String key : scores.keySet()){
			KeyDQ keyDQ = new KeyDQ(key, query.getId());
			if(features.containsKey(keyDQ)==false){
				features.put(keyDQ, new ArrayList<Double>());
				features.get(keyDQ).add(scores.get(key));
			}else{
				features.get(keyDQ).add(scores.get(key));
				
			}
		}
	}

}
