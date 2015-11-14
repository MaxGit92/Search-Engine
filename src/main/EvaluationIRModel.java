package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import evaluation.EvalIRModel;
import evaluation.EvalMeasure;
import evaluation.IRList;
import evaluation.PrecisionMoyenne;
import evaluation.PrecisionRappel;
import evaluation.Query;
import evaluation.QueryParser_CISI_CACM;
import indexation.Index;
import indexation.Stemmer;
import indexation.TextRepresenter;
import modeles.BM25;
import modeles.Hits;
import modeles.IRmodel;
import modeles.LanguageModel;
import modeles.ModeleRandomWalk;
import modeles.PageRank;
import modeles.Vectoriel;
import modeles.Weighter;
import modeles.WeighterVectoriel1;
import modeles.WeighterVectoriel2;
import modeles.WeighterVectoriel3;

public class EvaluationIRModel {
	
	public static void main(String args[]) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException{
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("crÃ©ation index");
		String indexName = "cisi_index"; // Ou "cacm_index"
		String invertedName = "cisi_inverted"; // Ou "cacm_inverted"
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
		Index indexObjet = Index.chargerObjetIndex("cisi.ser"); // ou "cacm.ser"
		
		System.out.println("creation weighter");
		Weighter weighter1 = new WeighterVectoriel1(indexName, invertedName, indexObjet);
		Weighter weighter2 = new WeighterVectoriel2(indexName, invertedName, indexObjet);
		Weighter weighter3 = new WeighterVectoriel3(indexName, invertedName, indexObjet);
		
		System.out.println("creation Modeles");
		Boolean normalized = true;
		IRmodel vectoriel1 = new Vectoriel(index, weighter1, normalized);
		IRmodel vectoriel2 = new Vectoriel(index, weighter2, normalized);
		IRmodel vectoriel3 = new Vectoriel(index, weighter3, normalized);

		// PARTIE MODELE DE LANGUE
		LanguageModel languageModel = new LanguageModel(index, "cisi_inverted", indexObjet, (float) 0.1); // bon lambda = 0.1 !!
		BM25 bm25 = new BM25(index, "cisi_inverted", indexObjet, 1.8, 0.75);

		// PARTIE RANDOMWALK
		float d=(float) 0.2;
		float epsilon=(float) 1e-3;
		int nbIter=300;
		PageRank pageRank = new PageRank(d, epsilon, nbIter);
		Hits hits = new Hits(nbIter);
		int n = 30;
		int k = 5;
		ModeleRandomWalk modelePageRankCisi1 = new ModeleRandomWalk(index, indexObjet, pageRank, languageModel, n, k); // On peut remplacer languageModele par un autre modèle
		ModeleRandomWalk modeleHitsCisi1 = new ModeleRandomWalk(index, indexObjet, hits, bm25, n, k); // On peut remplacer bm25 par un autre modèle

		System.out.println("creation query et queryStem");
		QueryParser_CISI_CACM queryParser = new QueryParser_CISI_CACM();
		queryParser.init("cisi.qry", "cisi.rel"); // Ou "cacm.qry, "cacm.rel"
		
		System.out.println("Test de EvalIRModel");
		List<IRmodel> iRmodels = new ArrayList<IRmodel>();
		iRmodels.add(vectoriel1);
		iRmodels.add(vectoriel2);
		iRmodels.add(vectoriel3); // Ajouter les modèles que l'on souhaite évaluer
		List<EvalMeasure> evalMeasrues = new ArrayList<EvalMeasure>();
		evalMeasrues.add(new PrecisionRappel(10));
		evalMeasrues.add(new PrecisionMoyenne()); // Ajouter les mesures que l'on souhaite évaluer (mais il n'y en a que 2)
		int nbQuery=3;
		int beginQuery=0;
		EvalIRModel evalIRModel = new EvalIRModel(iRmodels, evalMeasrues, queryParser, nbQuery, beginQuery);
		evalIRModel.allEvalsForAllModels();
		System.out.println(evalIRModel.toString());
	}
	
}
