package modeles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Hits extends RandomWalk{

	private int nbIter;
	
	public Hits(int nbIter) {
		super();
		this.nbIter = nbIter;
	}

	/**
	 * Mise � jour de A selon l'algorithme de Hits
	 * @param graphe
	 * @param a
	 * @param h
	 * @return
	 */
	public Map<String, Double> majA(Map<String, HashSet<String>> graphe, Map<String, Double> a, Map<String, Double> h){
		Map<String, Double> aRes = new HashMap<String, Double>();
		double maj = 0;
		for(String noeud : a.keySet()){
			maj = 0;
			for(String lien : graphe.keySet()){
				if(graphe.get(lien).contains(noeud))
					maj += h.get(lien);
			}
			aRes.put(noeud, maj*1.0/calculNorme(h));
		}
		return aRes;
	}
	
	/**
	 * Mise � jour de H selon l'algorithme de Hits
	 * @param graphe
	 * @param h
	 * @param a
	 * @return
	 */
	public Map<String, Double> majH(Map<String, HashSet<String>> graphe, Map<String, Double> h, Map<String,Double> a){
		Map<String, Double> hRes = new HashMap<String, Double>();
		double maj = 0;
		for(String noeud : h.keySet()){
			maj = 0;
			for(String lien : graphe.get(noeud)){
				if(a.get(lien)==null){
					continue;
				}
				maj += a.get(lien);
			}
			hRes.put(noeud, maj*1.0/calculNorme(a));
		}
		return hRes;
	}
	
	/**
	 * Calcul de la norme du vecteur mu
	 * @param mu
	 * @return
	 */
	public double calculNorme(Map<String, Double> mu){
		double res = 0;
		for(String key : mu.keySet()){
			res += mu.get(key) * mu.get(key);
		}
		return res;
	}
	
	@Override
	public Map<String, Double> marcheAleatoire(Map<String, HashSet<String>> graphe) {
		// Initialisation de a et h
		Map<String, Double> a = new HashMap<String, Double>();
		Map<String, Double> h = new HashMap<String, Double>();
		for(String noeud : graphe.keySet()){
			a.put(noeud, (double) 1);
			h.put(noeud, (double) 1);
		}
		for(int i=0; i<nbIter; i++){
			a=majA(graphe,a,h);
			h=majH(graphe,h,a);
		}
		return a;
	}

	@Override
	public String toString(){
		return "Modele Hits";
	}
	
}
