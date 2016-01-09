package diversite;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import modeles.ValueComparator;

public class DissimilariteMoyenne extends DiversiteGlouton{

	private float alpha;
	public DissimilariteMoyenne(Similarite similarite, float alpha) {
		super(similarite);
		this.alpha = alpha;
	}

	@Override
	public TreeMap<String, Double> diversify(TreeMap<String, Double> ranking) throws IOException {
		Map<String, Double> newRanking = new HashMap<String, Double>();
		// Nous remettons tout dans une HashMap (car sinon on ne peut pas faire de get !!!!)
		newRanking.putAll(ranking);
		// Nous créons un tableau ordonné par le ranking initial
		Set<String> ids = ranking.keySet();
		String[] idDocsRanking = ids.toArray(new String[ids.size()]);
		// Nous calculons le score de diversité
		for(int i=0; i<idDocsRanking.length-1; i++){
			if(i==0){
				newRanking.put(idDocsRanking[i], Double.MAX_VALUE);
				continue;
			}
			if(i==idDocsRanking.length-1){
				newRanking.put(idDocsRanking[i], Double.MIN_VALUE);
				continue;
			}
			Double newScore = alpha*newRanking.get(idDocsRanking[i]) - (1-alpha)*(1/(i))*similarite.similariteSum(idDocsRanking, i);
			newRanking.put(idDocsRanking[i], newScore);
		}
		ValueComparator comparateur = new ValueComparator(newRanking);
		TreeMap<String,Double> resRanking = new TreeMap<String,Double>(comparateur);
		resRanking.putAll(newRanking);
		return resRanking;
	}

	@Override
	public TreeMap<String, Double> diversify(TreeMap<String, Double> ranking, int n) throws IOException {
		Map<String, Double> newRanking = new HashMap<String, Double>();
		// Nous remettons tout dans une HashMap (car sinon on ne peut pas faire de get !!!!)
		newRanking.putAll(ranking);
		// Nous créons un tableau ordonné par le ranking initial
		Set<String> ids = ranking.keySet();
		String[] idDocsRanking = ids.toArray(new String[ids.size()]);
		// Nous calculons le score de diversité
		if(n>idDocsRanking.length) n=idDocsRanking.length;
		for(int i=0; i<n; i++){
			if(i==0){
				newRanking.put(idDocsRanking[i], Double.MAX_VALUE);
				continue;
			}
			Double newScore = newRanking.get(idDocsRanking[i]) - (1-alpha)*(1/(i))*similarite.similariteSum(idDocsRanking, i);
			newRanking.put(idDocsRanking[i], newScore);
		}
		ValueComparator comparateur = new ValueComparator(newRanking);
		TreeMap<String,Double> resRanking = new TreeMap<String,Double>(comparateur);
		resRanking.putAll(newRanking);
		return resRanking;
	}

}
