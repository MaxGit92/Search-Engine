package modeles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import evaluation.Query;
import modeles.IRmodel.ValueComparator;

public abstract class IRmodelFeaturer {
	protected RandomAccessFile index;
	
	public IRmodelFeaturer(RandomAccessFile index) {
		super();
		this.index = index;
	}
	
	/**
	 *  Renvoie les scores des documents pour une requÃªte passÃ©e en paramÃ¨tre
	 *  A la différence de IRmodel, c'est un objet Query passé en paramètre
	 *  car plus simple pour la récupération des id avec les featurers
	 * @param query
	 * @return HashMap<String, Double>
	 * @throws IOException 
	 */
	protected abstract HashMap<String, Double> getScores(Query query) throws IOException;
	
	/**
	 *  Renvoie une liste de couples (document-socre) ordonnÃ© par scores decroissants 
	 * @param query
	 * @return HashMap<String,Double>
	 * @throws IOException 
	 */
	public TreeMap<String,Double> getRanking(Query query) throws IOException{
		HashMap<String,Double> scores = getScores(query);
		ValueComparator comparateur = new ValueComparator(scores);
		TreeMap<String,Double> resTree = new TreeMap<String,Double>(comparateur);
		resTree.putAll(scores);
		//scores.clear();
		//scores.putAll(resTree);
		return resTree;
	}
	
	protected class ValueComparator implements Comparator<String> {
		Map<String, Double> base;
		public ValueComparator(Map<String, Double> base) {
		this.base = base;
	}
		@Override
		public int compare(String o1, String o2) {
			if(base.get(o1) <= base.get(o2)){
				return 1;
			}
			return -1;
		}
	}
}
