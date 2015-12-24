package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import diversite.DissimilariteMoyenne;
import diversite.Diversite;
import diversite.MMR;
import diversite.Similarite;
import diversite.SimilariteBM25;
import diversite.SimilariteLanguageModel;
import diversite.SimilariteVectoriel;
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
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;

public class TestMultimedia {

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("crÃ©ation index");
//		String indexName = "easy235_text_index";
//		String invertedName = "easy235_text_inverted";
//		String relevantName = "easy235_gt.txt";
//		String queryName = "easy235_query.txt";
		String indexName = "easyCLEF08_text_index";
		String invertedName = "easyCLEF08_text_inverted";
		String relevantName = "easyCLEF08_gt.txt";
		String queryName = "easyCLEF08_query.txt";
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
//		Index indexObjet = IndexMultimedia.chargerObjetIndex("easy235.ser");
		Index indexObjet = IndexMultimedia.chargerObjetIndex("easyCLEF08.ser");

		
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
		Query query = queryParser.getQuery(5); // Attention ï¿½ ne pas mettre un numQuery au delas du nombre de query
		System.out.println(query.getClusters());		
		
		HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());

		System.out.println("calcul ranking");
		TreeMap<String, Double> resultatRequete = vectoriel1.getRanking(queryStem); // Utiliser un des modï¿½les crï¿½ï¿½s
		//TreeMap<String, Double> resultatRequete = languageModel.getRanking(queryStem); // Utiliser un des modï¿½les crï¿½ï¿½s
		System.out.println("ranking\n" +resultatRequete);
		
		System.out.println("Calcul diversité");
		Similarite similariteVectoriel = new SimilariteVectoriel(indexObjet, index, inverted, weighter1);
		Similarite similariteLanguageModel = new SimilariteLanguageModel(indexObjet, index, inverted);
		Similarite similariteBM25 = new SimilariteBM25(indexObjet, index, inverted);
		float alpha=(float) 0.7;
		Diversite mmr = new MMR(similariteVectoriel, alpha);
		//Diversite mmr = new MMR(similariteLanguageModel, alpha);
		Diversite dissimilariteMoyenne = new DissimilariteMoyenne(similariteVectoriel, alpha);
		//Diversite dissimilariteMoyenne = new DissimilariteMoyenne(similariteLanguageModel, alpha);
		//TreeMap<String, Double> resultatRequeteDiversite = mmr.diversify(resultatRequete, 20);
		TreeMap<String, Double> resultatRequeteDiversite = dissimilariteMoyenne.diversify(resultatRequete, 20);


		System.out.println("creation measure");
		IRList irList = new IRList(query, resultatRequete);
		IRList irListDiv = new IRList(query, resultatRequeteDiversite);
		PrecisionRappel precisionRappel = new PrecisionRappel(20);
		PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
		PrecisionAtN precisionAtn = new PrecisionAtN(20);
		ClusterRecall clusterRecall = new ClusterRecall(8, 20);
		
		System.out.println("evaluation modele");
		//List<Double> resPM = precisionMoyenne.eval(irList);
		//List<Double> resPR = precisionRappel.eval(irList);
		List<Double> resPRN = precisionAtn.eval(irList);
		List<Double> resCR = clusterRecall.eval(irList);
		List<Double> resPRNDiv = precisionAtn.eval(irListDiv);
		List<Double> resCRDiv = clusterRecall.eval(irListDiv);
		//System.out.println("Prï¿½cision moyenne = "+resPM.get(0) + "\n" + "Prï¿½cision Rappel = " + resPR);
		System.out.println("P@20 = "+resPRN.get(0) + "\nCluster Recall = " +resCR);
		System.out.println("P@20 Diversifié = "+resPRNDiv.get(0) + "\nCluster Recall Diversifié = " +resCRDiv);

		
		
	}

}
