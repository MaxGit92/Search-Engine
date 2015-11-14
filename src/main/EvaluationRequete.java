package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import evaluation.IRList;
import evaluation.PrecisionMoyenne;
import evaluation.PrecisionRappel;
import evaluation.Query;
import evaluation.QueryParser_CISI_CACM;
import features.Featurer;
import features.FeaturersList;
import features.KeyDQ;
import indexation.Index;
import indexation.Stemmer;
import indexation.TextRepresenter;
import modeles.BM25;
import modeles.Hits;
import modeles.IRmodel;
import modeles.LanguageModel;
import modeles.LinearMetaModel;
import modeles.ModeleRandomWalk;
import modeles.PageRank;
import modeles.Vectoriel;
import modeles.Weighter;
import modeles.WeighterVectoriel1;
import modeles.WeighterVectoriel2;
import modeles.WeighterVectoriel3;
import modeles.WeighterVectoriel4;
import modeles.WeighterVectoriel5;

public class EvaluationRequete {

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("création index");
		String indexName = "cacm_index"; // Ou "cacm_index"
		String invertedName = "cacm_inverted"; // Ou "cacm_inverted"
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
		Index indexObjet = Index.chargerObjetIndex("cacm.ser"); // ou "cacm.ser"
		
		System.out.println("creation weighter");
		Weighter weighter1 = new WeighterVectoriel1(indexName, invertedName, indexObjet);
		Weighter weighter2 = new WeighterVectoriel2(indexName, invertedName, indexObjet);
		Weighter weighter3 = new WeighterVectoriel3(indexName, invertedName, indexObjet);
		Weighter weighter4 = new WeighterVectoriel4(indexName, invertedName, indexObjet);
		Weighter weighter5 = new WeighterVectoriel5(indexName, invertedName, indexObjet);
		
		System.out.println("creation Modeles");
		Boolean normalized = true;
		IRmodel vectoriel1 = new Vectoriel(index, weighter1, normalized);
		IRmodel vectoriel2 = new Vectoriel(index, weighter2, normalized);
		IRmodel vectoriel3 = new Vectoriel(index, weighter3, normalized);
		IRmodel vectoriel4 = new Vectoriel(index, weighter4, normalized);
		IRmodel vectoriel5 = new Vectoriel(index, weighter5, normalized);

		// PARTIE MODELE DE LANGUE
		LanguageModel languageModel = new LanguageModel(index, "cacm_inverted", indexObjet, (float) 0.1); // bon lambda = 0.1 !!
		BM25 bm25 = new BM25(index, "cacm_inverted", indexObjet, 1.8, 0.75);

		// PARTIE RANDOMWALK
		float d=(float) 0.2;
		float epsilon=(float) 1e-3;
		int nbIter=300;
		PageRank pageRank = new PageRank(d, epsilon, nbIter);
		Hits hits = new Hits(nbIter);
		int n = 50;
		int k = 5;
		ModeleRandomWalk modelePageRankCisi1 = new ModeleRandomWalk(index, indexObjet, pageRank, languageModel, n, k); // On peut remplacer languageModele par un autre mod�le
		ModeleRandomWalk modeleHitsCisi1 = new ModeleRandomWalk(index, indexObjet, hits, bm25, n, k); // On peut remplacer bm25 par un autre mod�le

		System.out.println("creation query et queryStem");
		QueryParser_CISI_CACM queryParser = new QueryParser_CISI_CACM();
		queryParser.init("cacm.qry", "cacm.rel"); // Ou "cacm.qry, "cacm.rel"
		
		TextRepresenter stemmer = new Stemmer();
		Query query = queryParser.getQuery(3); // Attention � ne pas mettre un numQuery au delas du nombre de query
		
		HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());
		// PARTIE FEATURER
        System.out.println("Chargement features");
		FeaturersList featurersList = (FeaturersList) Featurer.chargerFeaturer("features_cacm.ser"); // "features_cacm.ser"
		System.out.println(featurersList.getFeatures().size());
		for(KeyDQ keyDQ : featurersList.getFeatures().keySet()){
			if(keyDQ.getIdDoc().equals("10"))
				System.out.println(keyDQ.getIdDoc() + " " + keyDQ.getIdQuery() + " " + featurersList.getFeatures().get(keyDQ).toString());
		}

		System.out.println("Apprentissage du m�ta mod�le");
		LinearMetaModel linearMetaModel = new LinearMetaModel(index,featurersList,queryParser,indexObjet);
		linearMetaModel.fit(200, (float)0.5, (float)1e-3, (float)0.3);


		System.out.println("calcul ranking");
		TreeMap<String, Double> resultatRequete = linearMetaModel.getRanking(query); // Utiliser un des mod�les cr��s
		System.out.println(resultatRequete);
		
		System.out.println("creation measure");
		IRList irList = new IRList(query, resultatRequete);
		PrecisionRappel precisionRappel = new PrecisionRappel(10);
		PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
		
		System.out.println("evaluation modele");
		List<Double> resPM = precisionMoyenne.eval(irList);
		List<Double> resPR = precisionRappel.eval(irList);

		System.out.println("Pr�cision moyenne = "+resPM.get(0) + "\n" + "Pr�cision Rappel = " + resPR);
	}

}
