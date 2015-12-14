package evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrecisionRappel extends EvalMeasure{
	private int nbLevels;
	
	public PrecisionRappel(int nbLevels) {
		super();
		this.nbLevels = nbLevels;
	}
	
	private Double maxPrecisionI(Map<String,Double> docRel, Map<String,Double> docSelect, float k){
		int nbDocRel = docRel.size();
		int nbDocDecouverts=0;
		int i=0;
		for( String docS: docSelect.keySet()){
			i++;
			if(docRel.containsKey(docS)){
				nbDocDecouverts++;
				if(rappel(nbDocRel, nbDocDecouverts)>=k)
					return precision(nbDocDecouverts,i);
			}
		}
		return 0.0;
	}

	@Override
	public List<Double> eval(IRList I) {
		float step = (float) (1.0/this.nbLevels);
		float k=step;
		List<Double> res=new ArrayList<Double>();
		while(k<=1){
			Double maxPrecisionI = maxPrecisionI(I.getQuery().getRelevants(), I.getDocuments(), k);
			res.add(maxPrecisionI);
			k+=step;
		}
		return res;
	}
	
	@Override
	public String toString(){
		return "Precision rappel";
	}

}
