package modeles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import evaluation.Query;
import evaluation.QueryParser;
import features.FeaturersList;
import features.KeyDQ;
import indexation.Index;

public class LinearMetaModel extends MetaModel{

	private Double[] theta;
	private QueryParser queryParser;
	private Index indexObjet;
	
	// On initialise theta � 1/n pour chaque valeur
	public LinearMetaModel(RandomAccessFile index, FeaturersList featurersList,
			QueryParser queryParser, Index indexObjet) {
		super(index, featurersList);
		this.queryParser = queryParser;
		this.indexObjet = indexObjet;
		int n = 0;
		// On veut savoir la taille d'un des �l�ment de la liste features
		for(KeyDQ key : featurersList.getFeatures().keySet()){
			n = featurersList.getFeatures().get(key).size();
			break;
		}
		this.theta = new Double[n];
		for(int i=0; i<n; i++)
			this.theta[i] = 1.0/n;
	}

	/**
	 * Donne le score de tous les document selon une requete et le theta
	 * Utilis�e dans le main pour l'exp�rimentation
	 */
	@Override
	public HashMap<String, Double> getScores(Query query) throws IOException {
		HashMap<String, Double> res = new HashMap<String, Double>();
		this.index.seek(0);
		String ligne;
		Double scoreDoc;
		int cpt;
		try {
			while((ligne = this.index.readLine()) != null){
				String idDoc = ligne.split(":")[0];
				scoreDoc = (double) 0;
				cpt = 0;
				List<Double> feat = featurersList.getFeatures(idDoc, query);
				for(Double f : feat){
					scoreDoc += theta[cpt]*f;
					cpt++;
				}
				res.put(idDoc, scoreDoc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Donne le score d'un document pour une requete selon le theta
	 * @param query
	 * @param idDoc
	 * @return
	 * @throws IOException
	 */
	public Double getScore(Query query, String idDoc) throws IOException {
		Double scoreDoc = (double) 0;
		int cpt = 0;
		List<Double> feat = featurersList.getFeatures(idDoc, query);
		for(Double f : feat){
			scoreDoc += theta[cpt]*f;
			cpt++;
		}
		return scoreDoc;
	}
	
	/**
	 * Permet d'avoir une Map avec toutes les query associ� � leur id pour pouvoir les r�cup�rer facilement
	 * @return
	 * @throws IOException
	 */
	public Map<String,Query> createListQuery() throws IOException{
		queryParser.getBr().seek(0);
		queryParser.getRel().seek(0);
		Map<String,Query> queries = new HashMap<String,Query>();
		Query q;
		while((q = queryParser.nextQuery())!=null)
			queries.put(q.getId(),q);
		return queries;
	}
	
	/**
	 * Permet d'avoir une Map avec nbPoucent query de train de features
	 * @param queries
	 * @param nbPourcent
	 * @return
	 * @throws IOException
	 */
	public Map<String,Query> createQueriesTrain(Map<String, Query> queries, float nbPourcent) throws IOException{
		Map<String,Query> queriesRes = new HashMap<String,Query>();
		int taille = (int) (queries.size()*nbPourcent);
		Random generator = new Random();

		List<KeyDQ> keysDQ = new ArrayList(featurersList.getFeatures().keySet());

		for(int i=0; i<taille; i++){
			KeyDQ keyDQ = keysDQ.get(generator.nextInt(keysDQ.size()));
			keysDQ.remove(keyDQ);
			queriesRes.put(keyDQ.getIdQuery(), queries.get(keyDQ.getIdQuery()));
		}
		return queriesRes;
	}

	/**
	 * Fonction qui entraine le mod�le et trouve un bon theta
	 * Il faut aussi chercher les extra param�tres
	 * @param nbIter
	 * @param nbPourcent
	 * @param alpha
	 * @param lambda
	 * @throws IOException
	 */
	public void fit(int nbIter, float nbPourcent, float alpha, float lambda) throws IOException{
		Map<String,Query> queries = createListQuery(); // On a toute les query acc�ssible grace � une map
		Map<String,Query> queriesTrain = createQueriesTrain(queries, nbPourcent); // On a la liste des queries en train
		Random generator = new Random();
		List<String> keysDQ = new ArrayList(queriesTrain.keySet());
		for(int i=0; i<nbIter; i++){
			// Tirage al�atoire de la query et des doc pertinents et non pertinents
			String idQuery = keysDQ.get(generator.nextInt(keysDQ.size()));
			Query queryTmp = queriesTrain.get(idQuery);
			
			List<String> keys = new ArrayList<String>(queryTmp.getRelevants().keySet());
			if(keys.isEmpty()) continue;
			String idDocPert = keys.get(generator.nextInt(keys.size()));
		
			keys = new ArrayList<String>(indexObjet.getDocFrom().getId());
			String idDocNonPert = keys.get(generator.nextInt(keys.size()));
			while(queryTmp.getRelevants().containsKey(idDocNonPert))
				idDocNonPert = keys.get(generator.nextInt(keys.size()));
			
			// Calculs des scores pour les docs non pert et pert			
			Double scoreDocPert = getScore(queryTmp, idDocPert);
			Double scoreDocNonPert = getScore(queryTmp, idDocNonPert);
			
			if(1-scoreDocPert + scoreDocNonPert > 0){
				// MAJ de theta
				List<Double> featPert = featurersList.getFeatures(idDocPert, queryTmp);
				List<Double> featNonPert = featurersList.getFeatures(idDocNonPert, queryTmp);
				for(int j=0; i<theta.length; i++){
					theta[j] += alpha*(featPert.get(j) - featNonPert.get(j));
					// R�gularisation L2
					theta[j] = (1-2*alpha*lambda)*theta[j];
				}
			}
		}


	}
	
	@Override
	public String toString(){
		return "Meta modele lineaire";
	}

}
