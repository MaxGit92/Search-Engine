package features;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import evaluation.Query;

public class FeaturersList extends Featurer{
	/*
	 * Contient plusieurs featurers et va calculer pour chacun le score obtenu.
	 * Tous ces scores seront mis dans son propre attribut features
	 */
	private static final long serialVersionUID = 1797844289028933648L;
	private List<Featurer> featurers; // La listes de featurers

	public FeaturersList(RandomAccessFile index, String nomFichier, List<Featurer> featurers) throws ClassNotFoundException, IOException {
		super(index, nomFichier);
		this.featurers = featurers;
	}

	public List<Featurer> getFeaturers() {
		return featurers;
	}

	public void setFeaturers(List<Featurer> featurers) {
		this.featurers = featurers;
	}

	/**
	 * Renvoie le score de chaque featurer selon l'id du Document et de la query
	 */
	@Override
	public List<Double> getFeatures(String idDoc, Query query) throws IOException {
		KeyDQ keyDQ = new KeyDQ(idDoc, query.getId());
		if(!features.containsKey(keyDQ)){
			setListeFeatures();
			enregisterFeaturer();
		}
		return features.get(keyDQ);
	}
	
	/**
	 * met à jour la table features de featurersList
	 * @throws IOException
	 */
	public void setListeFeatures() throws IOException{
		for(Featurer featurer : featurers){
			Map<KeyDQ, List<Double>> feat = featurer.getFeatures();
			for(KeyDQ keyDQ : feat.keySet()){
				if(features.containsKey(keyDQ)){
					features.get(keyDQ).addAll(featurer.getFeatures().get(keyDQ));
				}else{
					features.put(keyDQ, featurer.getFeatures().get(keyDQ));
				}
			}
		}
	}

	/**
	 * Met à jour les features de chaque objet featurer selon une requete query
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Override
	public void setListeFeatures(Query query) throws IOException, InterruptedException, ExecutionException {
		System.out.println("Query numéro : "+query.getId());
		for(Featurer featurer : featurers)
			featurer.setListeFeatures(query);
	}
	
	/**
	 * Met à jour les features de chaque objet featurer selon une liste de requete
	 * puis met à jour features de FeaturersList
	 * @param queries
	 * @throws IOException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void setListeFeatures(List<Query> queries) throws IOException, InterruptedException, ExecutionException {
		for(Query q : queries){
			setListeFeatures(q);
		}
		setListeFeatures();	
	}
	
}
