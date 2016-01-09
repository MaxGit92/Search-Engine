package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import diversite.Clustering;
import diversite.ClusteringKMeans;
import diversite.DissimilariteMoyenne;
import diversite.IDiversite;
import diversite.MMR;
import diversite.PRCRang;
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

public class TestEvaluationDiversite {
	public static void main(String args[]) throws Exception{
		/*
		 * PACKAGE evaluation
		 */
		String indexName = "easyCLEF08_text_index";
		String invertedName = "easyCLEF08_text_inverted";
		String relevantName = "easyCLEF08_gt.txt";
		String queryName = "easyCLEF08_query.txt";
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
		Index indexObjet = IndexMultimedia.chargerObjetIndex("easyCLEF08.ser");

		
		Weighter weighter1 = new WeighterVectoriel1(indexName, invertedName, indexObjet);
		Weighter weighter2 = new WeighterVectoriel2(indexName, invertedName, indexObjet);
		Weighter weighter3 = new WeighterVectoriel3(indexName, invertedName, indexObjet);
		Weighter weighter4 = new WeighterVectoriel4(indexName, invertedName, indexObjet);
		Weighter weighter5 = new WeighterVectoriel5(indexName, invertedName, indexObjet);
		
		Boolean normalized = true;
		IRmodel vectoriel1 = new Vectoriel(index, weighter1, normalized);
		IRmodel vectoriel2 = new Vectoriel(index, weighter2, normalized);
		IRmodel vectoriel3 = new Vectoriel(index, weighter3, normalized);
		IRmodel vectoriel4 = new Vectoriel(index, weighter4, normalized);
		IRmodel vectoriel5 = new Vectoriel(index, weighter5, normalized);

		LanguageModel languageModel = new LanguageModel(index, invertedName, indexObjet, (float) 0.1); // bon lambda = 0.1 !!
		BM25 bm25 = new BM25(index, invertedName, indexObjet, 1.8, 0.75);

		QueryParser queryParser = new QueryParser_Multimedia();
		queryParser.init(queryName, relevantName);
		TextRepresenter stemmer = new Stemmer();

		int n=30; // nb doc à diversifier
		int nbClusters=5;
		int nbIter=200;
		
		PrecisionAtN precisionAtn = new PrecisionAtN(n);
		ClusterRecall clusterRecall = new ClusterRecall(nbClusters, n);
		
		Clustering kMeans = new ClusteringKMeans(index, indexObjet, nbClusters, nbIter);
		
		Similarite similariteVectoriel = new SimilariteVectoriel(indexObjet, index, inverted, weighter1);
		Similarite similariteLanguageModel = new SimilariteLanguageModel(indexObjet, index, inverted);
		Similarite similariteBM25 = new SimilariteBM25(indexObjet, index, inverted);
		
		float alpha=(float) 0.5;
		IDiversite mmr = new MMR(similariteVectoriel, alpha);
		//IDiversite mmr = new MMR(similariteLanguageModel, alpha);
		IDiversite dissimilariteMoyenne = new DissimilariteMoyenne(similariteVectoriel, alpha);
		//IDiversite dissimilariteMoyenne = new DissimilariteMoyenne(similariteLanguageModel, alpha);
		
		PRCRang prcRang = new PRCRang(kMeans);
		
		System.out.println("Calcul moyenne mesure");
		Query query = null;
		List<Double> totalPrec = new ArrayList<Double>();
		List<Double> totalCR = new ArrayList<Double>();
		List<Double> totalPrecDivDiss1 = new ArrayList<Double>();
		List<Double> totalCRDivDiss1 = new ArrayList<Double>();
		List<Double> totalPrecDivDiss2 = new ArrayList<Double>();
		List<Double> totalCRDivDiss2 = new ArrayList<Double>();
		List<Double> totalPrecDivClu = new ArrayList<Double>();
		List<Double> totalCRDivClu = new ArrayList<Double>();

		while((query = queryParser.nextQuery())!=null){
			System.out.println(query.getId());
			HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());

			TreeMap<String, Double> resultatRequete = vectoriel1.getRanking(queryStem);
			//TreeMap<String, Double> resultatRequete = languageModel.getRanking(queryStem);
			TreeMap<String, Double> resultatRequeteDiversiteDissimilarite1 = mmr.diversify(resultatRequete, n);
			TreeMap<String, Double> resultatRequeteDiversiteDissimilarite2 = dissimilariteMoyenne.diversify(resultatRequete, n);
			TreeMap<String, Double> resultatRequeteDiversiteCluster = prcRang.diversify(resultatRequete, n);
			IRList irList = new IRList(query, resultatRequete);
			IRList irListDivDiss1 = new IRList(query, resultatRequeteDiversiteDissimilarite1);
			IRList irListDivDiss2 = new IRList(query, resultatRequeteDiversiteDissimilarite2);
			IRList irListDivClu = new IRList(query, resultatRequeteDiversiteCluster);

			totalPrec.add(precisionAtn.eval(irList).get(0));
			totalCR.add(clusterRecall.eval(irList).get(0));
			totalPrecDivDiss1.add(precisionAtn.eval(irListDivDiss1).get(0));
			totalCRDivDiss1.add(clusterRecall.eval(irListDivDiss1).get(0));
			totalPrecDivDiss2.add(precisionAtn.eval(irListDivDiss2).get(0));
			totalCRDivDiss2.add(clusterRecall.eval(irListDivDiss2).get(0));
			totalPrecDivClu.add(precisionAtn.eval(irListDivClu).get(0));
			totalCRDivClu.add(clusterRecall.eval(irListDivClu).get(0));
			
		}
		
		int taille = totalCRDivClu.size();
		Double moyP=0.0;
		Double moyCR=0.0;
		Double moyPDiss1=0.0;
		Double moyPDiss2=0.0;
		Double moyPClu=0.0;
		Double moyCRDiss1=0.0;
		Double moyCRDiss2=0.0;
		Double moyCRClu=0.0;
		for(int i=0; i<taille; i++){
			moyP+=totalPrec.get(i);
			moyCR+=totalCR.get(i);
			moyPDiss1+=totalPrecDivDiss1.get(i);
			moyPDiss2+=totalPrecDivDiss2.get(i);
			moyPClu+=totalPrecDivClu.get(i);
			moyCRDiss1+=totalCRDivDiss1.get(i);
			moyCRDiss2+=totalCRDivDiss2.get(i);
			moyCRClu+=totalCRDivClu.get(i);
		}
		System.out.println("Precision@"+n+" moyenne normale = " + moyP/taille);
		System.out.println("Precision@"+n+" moyenne pour MMR = " + moyPDiss1/taille);
		System.out.println("Precision@"+n+" moyenne pour dissimiralité moyenne = " + moyPDiss2/taille);
		System.out.println("Precision@"+n+" moyenne pour clustering = " + moyPClu/taille);
		System.out.println("Cluster Recall"+" moyen normal = " + moyCR/taille);
		System.out.println("Cluster Recall"+" moyen pour MMR = " + moyCRDiss1/taille);
		System.out.println("Cluster Recall"+" moyen pour dissimiralité moyenne = " + moyCRDiss2/taille);
		System.out.println("Cluster Recall"+" moyen pour clustering = " + moyCRClu/taille);

	}
}
