package diversite;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import modeles.ValueComparator;

public class ProgDin extends DiversiteGlouton{

	private float alpha;
	private double[][] backpoint;
	public ProgDin(Similarite similarite, float alpha) {
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
		
		backpoint = new double[ranking.size()][ranking.size()];
		backpoint[0][0] = newRanking.get(idDocsRanking[0]); // initialisation
		for(int i=1; i<backpoint.length; i++){
			for(int j=0; j<=i; i++){
				double maxValue = Double.MIN_VALUE;
				int posMax = 0;
				for(int k=0; k<=i; k++){
					double value = backpoint[i-1][k]  + newRanking.get(idDocsRanking[k]) - (1-alpha)*similarite.similariteMax(idDocsRanking, k);;
					if(value > maxValue){
						maxValue = value; 
						posMax = k;
					}
				}
				backpoint[i][j] = backpoint[i-1][posMax] + newRanking.get(idDocsRanking[i]) - (1-alpha)*similarite.similariteMax(idDocsRanking, i);
			}
		}
		
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
			Double newScore = newRanking.get(idDocsRanking[i]) - (1-alpha)*similarite.similariteMax(idDocsRanking, i);
			newRanking.put(idDocsRanking[i], newScore);
		}
		ValueComparator comparateur = new ValueComparator(newRanking);
		TreeMap<String,Double> resRanking = new TreeMap<String,Double>(comparateur);
		resRanking.putAll(ranking);
		return resRanking;
	}
	@Override
	public TreeMap<String, Double> diversify(TreeMap<String, Double> ranking, int n) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
