package modeles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import indexation.Index;

public class PageRank extends RandomWalk{

	private float d;
	private float epsilon;
	private int nbIter;

	public PageRank(float d, float epsilon, int nbIter) {
		super();
		this.d = d;
		this.epsilon = epsilon;
		this.nbIter = nbIter;
	}
	/**
	 * Calcul de A*mu^t selon algorithme PageRank
	 * @param noeud
	 * @param mu
	 * @param graphe
	 * @return
	 */
	public double calculValue(String noeud, Map<String, Double> mu, Map<String, HashSet<String>> graphe){
		double res = 0;
		if(graphe.get(noeud)==null) return res;
		for(String lien : graphe.get(noeud)){
			if(graphe.get(lien)==null) continue;
			res += mu.get(lien)/graphe.get(lien).size();
		}
		return res;
	}
	
	/**
	 * Fonction qui initialise mu de PageRank
	 * @param N
	 * @param graphe
	 * @return
	 */
	public Map<String, Double> initialiserMu(long N, Map<String, HashSet<String>> graphe){
		Map<String, Double> mu = new HashMap<String, Double>();
		for(String clefNoeud : graphe.keySet()){
			mu.put(clefNoeud, 1.0/N);
		}
		return mu;
	}
	
	/**
	 * Calcul de la norme de mu
	 * @param mu
	 * @return
	 */
	public double calculNorme(Map<String, Double> mu){
		double res = 0;
		for(String key : mu.keySet()){
			res += mu.get(key) * mu.get(key);
		}
		return Math.sqrt(res);
	}
	
	@Override
	public Map<String, Double> marcheAleatoire(Map<String, HashSet<String>> graphe) {
		long N = graphe.size();
		Map<String, Double> mu = initialiserMu(N, graphe);
		double value;
		
		double norme = 10000;
		double ancienneNorme = 500;
		int cpt = 0;
		while(Math.abs(norme-ancienneNorme) != 0 && cpt<nbIter){
			if(cpt%100==0) System.out.println("Norme = " + norme);
			for(String clefNoeud : graphe.keySet()){
				value = (1-d)*1.0/N + d*calculValue(clefNoeud, mu, graphe);
				mu.put(clefNoeud, value);
				for(String noeud : graphe.get(clefNoeud)){
					if(!mu.containsKey(noeud)) continue;
					value = (1-d)*1.0/N + d*calculValue(noeud, mu, graphe);
					mu.put(noeud, value);
				}
			}
			cpt++;
			ancienneNorme = norme;
			norme = calculNorme(mu);
		}
		return mu;
	}

	public float getD() {
		return d;
	}

	public void setD(float d) {
		this.d = d;
	}

	public float getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(float epsilon) {
		this.epsilon = epsilon;
	}

	public int getNbIter() {
		return nbIter;
	}

	public void setNbIter(int nbIter) {
		this.nbIter = nbIter;
	}
	
	@Override
	public String toString(){
		return "Modele PageRank";
	}

}
