package diversite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import modeles.ValueComparator;

public class PRCRang extends DiversiteCluster{

	public PRCRang(Clustering clustering) {
		super(clustering);
	}

	@Override
	public TreeMap<String, Double> diversify(TreeMap<String, Double> ranking) throws Exception {
		// On détermine les clusters
		Map<String, Integer> clusters = clustering.clustering();
		// On crée une map qui contient les cluster avec leur document dans l'ordre de ranking
		Map<Integer, ArrayList<String>> rankingClusters = new HashMap<Integer, ArrayList<String>>();
		for(int i=0; i<clustering.getNbCluster(); i++){
			rankingClusters.put(i, new ArrayList<String>());
		}
		int cpt = 0;
		Double maxValue = Collections.max(ranking.values()); // Permet de mettre les score à la fin
		List<Integer> orderedCluster = new ArrayList<Integer>(); // contient l'ordre des clusters par meilleur doc
		for(String doc : ranking.keySet()){
			rankingClusters.get(clusters.get(doc)).add(doc);
			if(!orderedCluster.contains(clusters.get(doc))) orderedCluster.add(clusters.get(doc));
		}

		Map<String, Double> newRanking = new HashMap<String, Double>();
		// Nous remettons tout dans une HashMap (car sinon on ne peut pas faire de get !!!!)
		newRanking.putAll(ranking);
		for(int i=0; i<ranking.size(); i++){
			for(Integer cluster : orderedCluster){
				if(rankingClusters.get(cluster).isEmpty()) continue;
				Double value = newRanking.get(rankingClusters.get(cluster).get(0));
				if(value==null)continue;
				newRanking.put(rankingClusters.get(cluster).remove(0), maxValue+ranking.size()-i);
				i++;
			}
			i--;
		}

		ValueComparator comparateur = new ValueComparator(newRanking);
		TreeMap<String,Double> resRanking = new TreeMap<String,Double>(comparateur);
		resRanking.putAll(newRanking);
		return resRanking;
	}

	@Override
	public TreeMap<String, Double> diversify(TreeMap<String, Double> ranking, int n) throws Exception {
		// On détermine les clusters
		Map<String, Integer> clusters = clustering.clustering(ranking, n);
		// On crée une map qui contient les cluster avec leur document dans l'ordre de ranking
		Map<Integer, ArrayList<String>> rankingClusters = new HashMap<Integer, ArrayList<String>>();
		for(int i=0; i<clustering.getNbCluster(); i++){
			rankingClusters.put(i, new ArrayList<String>());
		}
		int cpt = 0;
		Double maxValue = Collections.max(ranking.values()); // Permet de mettre les score à la fin
		List<Integer> orderedCluster = new ArrayList<Integer>(); // contient l'ordre des clusters par meilleur doc
		for(String doc : ranking.keySet()){
			rankingClusters.get(clusters.get(doc)).add(doc);
			if(!orderedCluster.contains(clusters.get(doc))) orderedCluster.add(clusters.get(doc));
			if(++cpt==n)break;
		}
		
		if(n>ranking.size()) n=ranking.size();
		Map<String, Double> newRanking = new HashMap<String, Double>();
		// Nous remettons tout dans une HashMap (car sinon on ne peut pas faire de get !!!!)
		newRanking.putAll(ranking);
		for(int i=0; i<n; i++){
			for(Integer cluster : orderedCluster){
				if(rankingClusters.get(cluster).isEmpty()) continue;
				Double value = newRanking.get(rankingClusters.get(cluster).get(0));
				if(value==null)continue;
				newRanking.put(rankingClusters.get(cluster).remove(0), maxValue+n-i);
				i++;
			}
			i--;
		}
		
		ValueComparator comparateur = new ValueComparator(newRanking);
		TreeMap<String,Double> resRanking = new TreeMap<String,Double>(comparateur);
		resRanking.putAll(newRanking);
		return resRanking;
	}

	
}
