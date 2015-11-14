package modeles;

import indexation.Index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.omg.CORBA.ValueBaseHolder;


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
