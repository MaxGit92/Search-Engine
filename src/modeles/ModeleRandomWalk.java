package modeles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import indexation.Index;

public class ModeleRandomWalk extends IRmodel{

	private IRmodel iRmodel;
	private RandomWalk randomWalk;
	private Index indexObjet;
	private int n; // nombre de documents seeds � consid�rer
	private int k; // nombre de liens entrants � consid�rer pour chaque document seed 

	public ModeleRandomWalk(RandomAccessFile index, Index indexObjet, RandomWalk randomWalk, IRmodel iRmodel, int n, int k) {
		super(index);
		this.randomWalk = randomWalk;
		this.indexObjet = indexObjet;
		this.iRmodel = iRmodel;
		this.n = n;
		this.k = k;
	}

	/**
	 * Creer le graphe avec les n meilleurs documents et
	 * k liens entrants consid�r�s comme seed
	 * @param scores
	 * @return
	 */
	public Map<String, HashSet<String>> creerGraphe(Map<String, Double> scores){
		Map<String,HashSet<String>> liens = indexObjet.getDocFrom().getLinksDoc();
		Map<String, HashSet<String>> graphe = new HashMap<String, HashSet<String>>();
		// Creation des seeds
		int cpt = 0;
		for(String key : scores.keySet()){
			if(cpt == n) break;
			graphe.put(key, liens.get(key));
			cpt++;
		}
		// Ajout dans le graphe des noeuds entrants
		cpt = 0;
		Random generator = new Random();
		HashMap<String, HashSet<String>> aux = new HashMap<String,HashSet<String>>(liens);
		while(cpt!=k && aux.size()>0){
			List<String> keys = new ArrayList<String>(aux.keySet());
			String randomKey = keys.get(generator.nextInt(keys.size()));
			if(liens.containsKey(randomKey)){
				graphe.put(randomKey, liens.get(randomKey));
				cpt++;
			}
			aux.remove(randomKey);
		}
		return graphe;
	}
	
	@Override
	protected HashMap<String, Double> getScores(HashMap<String, Integer> query) throws IOException, InterruptedException, ExecutionException {
		TreeMap<String,Double> scoresIrmodel = iRmodel.getRanking(query);
		HashMap<String, Double> scoresMarche = (HashMap<String, Double>) this.randomWalk.marcheAleatoire(creerGraphe(scoresIrmodel));
		
		Random generator = new Random();
		HashMap<String, Double> aux = new HashMap<String,Double>(scoresIrmodel);
		while(aux.size()>0){
			List<String> keys = new ArrayList<String>(aux.keySet());
			String randomKey = keys.get(generator.nextInt(keys.size()));
			if(scoresMarche.containsKey(randomKey)){
				aux.remove(randomKey);
				continue;
			}
			scoresMarche.put(randomKey, (double)0);
			aux.remove(randomKey);
		}
		return scoresMarche;
	}
	
	@Override
	public String toString(){
		return randomWalk.toString();
	}

}
