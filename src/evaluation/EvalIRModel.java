package evaluation;

import indexation.TextRepresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import modeles.IRmodel;
import indexation.Stemmer;


public class EvalIRModel {
	private List<IRmodel> iRmodels; // liste des mod�les de recherche que nous voulons �valuer.
	private List<EvalMeasure> evalMeasures; // liste des mesures d'�valuation pour les mod�les.
	
	private QueryParser_CISI_CACM parser; // le parser de query
	private int nbQuery; // nombre de query à évaluer
	private int beginQuery; // Pour savoir à quel query de notre fichier on commence à évaluer
	private List<EvaluationRepresentation> listEval; //le resultat de allEvalsForAllModels
	
	public EvalIRModel(List<IRmodel> iRmodels,
			List<EvalMeasure> evalMeasures, 
			QueryParser_CISI_CACM parser, int nbQuery, int beginQuery) {
		super();
		this.iRmodels = iRmodels;
		this.evalMeasures = evalMeasures;
		this.parser = parser;
		this.nbQuery = nbQuery;
		this.beginQuery = beginQuery;
		this.listEval = new ArrayList<EvaluationRepresentation>();
	}

	/**
	 * Initialisation pour parcourrir toutes les query
	 * @param iRmodels
	 * @param evalMeasures
	 * @param parser
	 */
	public EvalIRModel(List<IRmodel> iRmodels,
			List<EvalMeasure> evalMeasures, 
			QueryParser_CISI_CACM parser) {
		super();
		this.iRmodels = iRmodels;
		this.evalMeasures = evalMeasures;
		this.parser = parser;
		this.nbQuery = -1;
		beginQuery = 0;
		this.listEval = new ArrayList<EvaluationRepresentation>();
	}

	public void allEvalsForAllModels() throws IOException, InterruptedException, ExecutionException{
		Query qTmp;
		TextRepresenter stemmer = new Stemmer();
		int cpt = 0;
		int cptBegin = 0;
		parser.getBr().seek(0);
		while((qTmp = this.parser.nextQuery()) != null && cpt<this.nbQuery){
			if(cptBegin<beginQuery){
				cptBegin++;
				continue;
			}
			HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(qTmp.getText());
			cpt++;
			System.out.println("Requete numéro " + cpt);
			for(IRmodel modele : this.iRmodels){
				TreeMap<String,Double> resultatRequete = modele.getRanking(queryStem);
				IRList irList = new IRList(qTmp, resultatRequete);
				for(EvalMeasure evalM : this.evalMeasures){
					List<Double> mesure = evalM.eval(irList);
					this.listEval.add(new EvaluationRepresentation(modele.toString(), evalM.toString(),qTmp,mesure));
				}
			}
		}
	}
	
	@Override
	public String toString(){
		String s = "";
		for(EvaluationRepresentation er : this.listEval){
			s+=er.toString();
			s+="\n";
		}
		return s;
	}
}
