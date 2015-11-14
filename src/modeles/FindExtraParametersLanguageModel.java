package modeles;

import indexation.Stemmer;
import indexation.TextRepresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import evaluation.IRList;
import evaluation.PrecisionMoyenne;
import evaluation.Query;
import evaluation.QueryParser;

public class FindExtraParametersLanguageModel {
	
	// Definition des parametres
	private LanguageModel languageModel;
	private QueryParser queryParser;
	private float pourcentageQueryTrain;
	private float lambdaMin; // la valeur min de lambda a tester
	private float lambdaMax; // la valeur max de lambda a tester
	private float lambdaStep; // le pas du lambda a tester
	
	// Constructeur
	public FindExtraParametersLanguageModel(LanguageModel languageModel,
			QueryParser queryParser, float pourcentageQueryTrain,
			float lambdaMin, float lambdaMax, float lambdaStep) {
		super();
		this.languageModel = languageModel;
		this.queryParser = queryParser;
		this.pourcentageQueryTrain = pourcentageQueryTrain;
		this.lambdaMin = lambdaMin;
		this.lambdaMax = lambdaMax;
		this.lambdaStep = lambdaStep;
	}
	
	// Retourne la liste de toutes les query, sert à pouvoir les mélanger aléatoirement pour
	// creer le trainset et testset
	public List<Query> createListQuery() throws IOException{
		queryParser.getBr().seek(0);
		queryParser.getRel().seek(0);
		List<Query> queries = new ArrayList<Query>();
		Query q;
		while((q = queryParser.nextQuery())!=null)
			queries.add(q);
		return queries;
	}
	
	public float findExtraParameters() throws IOException, InterruptedException, ExecutionException{
		
		//Création du stemmer pour les query
		TextRepresenter stemmer = new Stemmer();
		
		// Récuperation des query
		List<Query> queries = createListQuery();
		
		// initialisation du train
		List<Query> train = new ArrayList<Query>();
		
		// calcul du nombre de query en train
		int trainSize = (int) (queries.size()*pourcentageQueryTrain);
		
		// Shuffle => création du trainSet et testSet avec un pourcentage du trainset definit dans le constructeur
		Collections.shuffle(queries);
		
		int i;
		// On ajoute les queries dans le train
		for(i = 0; i<trainSize; i++)
			train.add(queries.get(i));
		
		// Nous choisissons comme critère d'évaluation la précision moyenne
		PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
		IRList irList;
		Double precision=(double) 0;
		Double maxPrecision=(double) 0;
		float maxLambda = lambdaMin;
		// Apprenitssage
		for(float lambda = lambdaMin; lambda <= lambdaMax; lambda+=lambdaStep){
			System.out.println("lambda : " + lambda);
			for(Query query : train){
				if(query.getRelevants().size()==0)continue;
				languageModel.setLambda(lambda);
				Map<String,Double> ranking = languageModel.getRanking(stemmer.getTextRepresentation(query.getText()));
				irList = new IRList(query, ranking);
				precision += precisionMoyenne.eval(irList).get(0);
			}
			precision=precision/train.size();
			if(precision > maxPrecision){
				maxPrecision = precision;
				maxLambda = lambda;
			}
			System.out.println("précision : "+precision);
			precision=(double) 0;
		}

		return maxLambda;
	}
}
