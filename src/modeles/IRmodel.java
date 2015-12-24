package modeles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;



public abstract class IRmodel {
	
	protected RandomAccessFile index;

	public IRmodel(RandomAccessFile index) {
		super();
		this.index = index;
	}
	
	/**
	 *  Renvoie les scores des documents pour une requête passée en paramètre
	 * @param query
	 * @return HashMap<String, Double>
	 * @throws IOException 
	 */
	protected abstract HashMap<String, Double> getScores(HashMap<String, Integer> query) throws IOException, InterruptedException, ExecutionException;
	
	/**
	 *  Renvoie une liste de couples (document-socre) ordonné par scores decroissants 
	 * @param query
	 * @return HashMap<String,Double>
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public TreeMap<String,Double> getRanking(HashMap<String,Integer> query) throws IOException, InterruptedException, ExecutionException{
		HashMap<String,Double> scores = getScores(query);
		ValueComparator comparateur = new ValueComparator(scores);
		TreeMap<String,Double> resTree = new TreeMap<String,Double>(comparateur);
		resTree.putAll(scores);
		//scores.clear();
		//scores.putAll(resTree);
		return resTree;
	}
	
	
	
	
}
