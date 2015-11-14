package modeles;

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
import indexation.Stemmer;
import indexation.TextRepresenter;

public class FindExtraParametersBM25 {
	// Definition des parametres
		private BM25 bm25;
		private QueryParser queryParser;
		private float pourcentageQueryTrain;
		private float bMin; // la valeur min de b a tester
		private float bMax; // la valeur max de b a tester
		private float bStep; // le pas du lambda a tester
		private float k1Min; // la valeur min de k1 a tester
		private float k1Max; // la valeur max de k1 a tester
		private float k1Step; // le pas du k1 a tester
		public FindExtraParametersBM25(modeles.BM25 bm25, QueryParser queryParser, float pourcentageQueryTrain,
				float bMin, float bMax, float bStep, float k1Min, float k1Max, float k1Step) {
			super();
			this.bm25 = bm25;
			this.queryParser = queryParser;
			this.pourcentageQueryTrain = pourcentageQueryTrain;
			this.bMin = bMin;
			this.bMax = bMax;
			this.bStep = bStep;
			this.k1Min = k1Min;
			this.k1Max = k1Max;
			this.k1Step = k1Step;
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
		
		public float[] findExtraParameters() throws IOException, InterruptedException, ExecutionException{
			
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
			float maxB = bMin;
			float maxK1 = k1Min;
			// CrossValidation
			
			for(float k1 = k1Min; k1 <= k1Max; k1+=k1Step){
				System.out.println("k1 : " + k1);
				bm25.setK1(k1);
				for(float b = bMin; b <= bMax; b+=bStep){
					System.out.println("b : "+b);
					bm25.setB(b);
					for(Query query : train){
						if(query.getRelevants().size()==0) continue;
						Map<String,Double> ranking = bm25.getRanking(stemmer.getTextRepresentation(query.getText()));
						irList = new IRList(query, ranking);
						precision += precisionMoyenne.eval(irList).get(0);
					}
					precision=precision/train.size();
					if(precision > maxPrecision){
						maxPrecision = precision;
						maxB = b;
						maxK1 = k1;
					}
					System.out.println("précision : "+precision);
					precision=(double) 0;
				}
			}
			float[] res = {maxK1,maxB};
			return res;
		}
}
