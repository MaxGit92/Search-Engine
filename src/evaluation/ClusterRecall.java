package evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClusterRecall extends EvalMeasure{
	private int nbSousThemes;
	private int n;
	
	public ClusterRecall(int nbSousThemes, int n){
		this.nbSousThemes = nbSousThemes;
		this.n = n;
	}
	
	/**
	 * Renvoie le cluster recall pour les n premiers documents
	 * @param I
	 * @param n
	 * @return
	 */
	// A MODIFIER NE PEUX PAS ENCORE ETRE IMPLEMENTE (10/12)
	public double clusterRecall(IRList I, int nbSousThemes, int n){
		int i = 0; // Compte le nombre de documents
		Set<String> clusters = new HashSet<String>();
		int nbSousThemesTrouves=0;
		for(String docS: I.getDocuments().keySet()){
			if(!clusters.contains(I.getQuery().getClusters().get(docS))){
				nbSousThemesTrouves++;
				clusters.add(I.getQuery().getClusters().get(docS));
			}
			i++;
			if(i==n) break;
		}
		return nbSousThemesTrouves*1.0/nbSousThemes;
	}
	
	@Override
	public List<Double> eval(IRList I) {
		List<Double> res = new ArrayList<Double>();
		res.add(clusterRecall(I, nbSousThemes, n));
		return res;
	}

}
