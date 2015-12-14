package evaluation;

import java.util.ArrayList;
import java.util.List;

public class PrecisionAtN extends EvalMeasure{

	private int n;
	
	public PrecisionAtN(int n){
		this.n = n;
	}
	
	/**
	 * Renvoie la précision en regardant n premiers documents seulement
	 * @param I
	 * @param n
	 * @return
	 */
	public double precisionNPremiersDoc(IRList I, int n){
		int i = 0; // Compte le nombre de documents
		int nbDocDecouverts=0;
		for(String docS: I.getDocuments().keySet()){
			if(I.getQuery().getRelevants().containsKey(docS)){
				nbDocDecouverts++;
			}
			i++;
			if(i==n) break;
		}
		return precision(nbDocDecouverts, i);
	}
	
	@Override
	public List<Double> eval(IRList I) {
		List<Double> res = new ArrayList<Double>();
		res.add(precisionNPremiersDoc(I, n));
		return res;
	}

}
