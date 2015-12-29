package evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterRecall extends EvalMeasure{
	private int nbSousThemes;
	private int n;
	private Map<String, Integer> clusters;
	
	public ClusterRecall(int nbSousThemes, int n, Map<String, Integer> clusters){
		this.nbSousThemes = nbSousThemes;
		this.n = n;
		this.clusters = clusters;
	}
	
	/**
	 * Renvoie le cluster recall pour les n premiers documents
	 * @param I
	 * @param n
	 * @return
	 */
	public double clusterRecall(IRList I, int nbSousThemes, int n){
		int i = 0; // Compte le nombre de documents
		Set<Integer> clusters = new HashSet<Integer>();
		int nbSousThemesTrouves=0;
		for(String docS: this.clusters.keySet()){
			if(!clusters.contains(this.clusters.get(docS))){
				nbSousThemesTrouves++;
				clusters.add(this.clusters.get(docS));
			}
			if(++i==n) break;
		}
		System.out.println(nbSousThemes);
		return nbSousThemesTrouves*1.0/nbSousThemes;
	}
	
	@Override
	public List<Double> eval(IRList I) {
		List<Double> res = new ArrayList<Double>();
		res.add(clusterRecall(I, nbSousThemes, n));
		return res;
	}

}
