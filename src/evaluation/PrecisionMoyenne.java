package evaluation;

import java.util.ArrayList;
import java.util.List;

public class PrecisionMoyenne extends EvalMeasure{

	@Override
	public List<Double> eval(IRList I) {
		int i = 0; // Compte le nombre de documents
		int nbDocDecouverts=0;
		double totalPrecision=0;
		List<Double> res=new ArrayList<Double>();
		for(String docS: I.getDocuments().keySet()){
			i++;
			if(I.getQuery().getRelevants().containsKey(docS)){
				nbDocDecouverts++;
				totalPrecision += precision(nbDocDecouverts,i);
				//res.add((1.0*totalPrecision)/I.getDocuments().size());
			}
		}
		if(nbDocDecouverts==0){
			res.add((double)0);
			return res;
		}
		res.add((totalPrecision*1.0)/nbDocDecouverts);
		return res;
	}
	

	@Override
	public String toString(){
		return "Precision moyenne";
	}
}
