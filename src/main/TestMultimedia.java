package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import evaluation.ClusterRecall;
import evaluation.IRList;
import evaluation.PrecisionAtN;
import evaluation.PrecisionMoyenne;
import evaluation.PrecisionRappel;
import evaluation.Query;
import evaluation.QueryParser;
import evaluation.QueryParser_Multimedia;
import indexation.Index;
import indexation.IndexMultimedia;
import indexation.Stemmer;
import indexation.TextRepresenter;
import modeles.BM25;
import modeles.IRmodel;
import modeles.LanguageModel;
import modeles.Vectoriel;
import modeles.Weighter;
import modeles.WeighterVectoriel1;
import modeles.WeighterVectoriel2;
import modeles.WeighterVectoriel3;
import modeles.WeighterVectoriel4;
import modeles.WeighterVectoriel5;

public class TestMultimedia {

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("création index");
		String indexName = "easy235_text_index";
		String invertedName = "easy235_text_inverted";
		String relevantName = "easy235_gt.txt";
		String queryName = "easy235_query.txt";
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
		Index indexObjet = IndexMultimedia.chargerObjetIndex("easy235.ser");
		
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
		LanguageModel languageModel = new LanguageModel(index, invertedName, indexObjet, (float) 0.1); // bon lambda = 0.1 !!
		BM25 bm25 = new BM25(index, invertedName, indexObjet, 1.8, 0.75);

		System.out.println("creation query et queryStem");
		QueryParser queryParser = new QueryParser_Multimedia();
		queryParser.init(queryName, relevantName);
		
		TextRepresenter stemmer = new Stemmer();
		Query query = queryParser.getQuery(3); // Attention � ne pas mettre un numQuery au delas du nombre de query
		System.out.println(query.getClusters());		
		
		HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());

		System.out.println("calcul ranking");
		TreeMap<String, Double> resultatRequete = languageModel.getRanking(queryStem); // Utiliser un des mod�les cr��s
		//System.out.println(resultatRequete);
		
		System.out.println("creation measure");
		IRList irList = new IRList(query, resultatRequete);
		PrecisionRappel precisionRappel = new PrecisionRappel(20);
		PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
		PrecisionAtN precisionAtn = new PrecisionAtN(10);
		ClusterRecall clusterRecall = new ClusterRecall(8, 3);
		
		System.out.println("evaluation modele");
		//List<Double> resPM = precisionMoyenne.eval(irList);
		//List<Double> resPR = precisionRappel.eval(irList);
		List<Double> resPRN = precisionAtn.eval(irList);
		List<Double> resCR = clusterRecall.eval(irList);
		//System.out.println("Pr�cision moyenne = "+resPM.get(0) + "\n" + "Pr�cision Rappel = " + resPR);
		System.out.println("P@20 = "+resPRN.get(0) + "\nCluster Recall = " +resCR);
		
	}

}
