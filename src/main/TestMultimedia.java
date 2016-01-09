package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

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
import evaluation.EvalIRModel;
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

	public static void main(String[] args) throws Exception {
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("crÃ©ation index");
		String indexName = "easyCLEF08_text_index";
		String invertedName = "easyCLEF08_text_inverted";
		String relevantName = "easyCLEF08_gt.txt";
		String queryName = "easyCLEF08_query.txt";
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
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
		Query query = queryParser.getQuery(10); // Attention a ne pas mettre un numQuery au delas du nombre de query
		System.out.println(query.getClusters());		
		HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());
		
		System.out.println("calcul ranking");
		TreeMap<String, Double> resultatRequete = vectoriel1.getRanking(queryStem); // Utiliser un des modeles crees
		//TreeMap<String, Double> resultatRequete = languageModel.getRanking(queryStem);
		System.out.println("ranking\n" +resultatRequete);
		
		System.out.println("Clustering");
		int nbClusters=4;
		int nbIter=200;
		Clustering kMeans = new ClusteringKMeans(index, indexObjet, nbClusters, nbIter);
		//Map<String, Integer> clusters = kMeans.clustering();
		//System.out.println(clusters);
		
		
		System.out.println("Calcul diversité avec similarité");
		Similarite similariteVectoriel = new SimilariteVectoriel(indexObjet, index, inverted, weighter1);
		Similarite similariteLanguageModel = new SimilariteLanguageModel(indexObjet, index, inverted);
		Similarite similariteBM25 = new SimilariteBM25(indexObjet, index, inverted);
		float alpha=(float) 0.3;
		IDiversite mmr = new MMR(similariteVectoriel, alpha);
		//Diversite mmr = new MMR(similariteLanguageModel, alpha);
		IDiversite dissimilariteMoyenne = new DissimilariteMoyenne(similariteVectoriel, alpha);
		//IDiversite dissimilariteMoyenne = new DissimilariteMoyenne(similariteLanguageModel, alpha);
		//TreeMap<String, Double> resultatRequeteDiversite = mmr.diversify(resultatRequete, 20);
		TreeMap<String, Double> resultatRequeteDiversiteDissimilarite = dissimilariteMoyenne.diversify(resultatRequete, 20);
		//System.out.println(resultatRequeteDiversiteDissimilarite);
		
		System.out.println("Calcul diversité avec clustering");
		PRCRang prcRang = new PRCRang(kMeans);
		TreeMap<String, Double> resultatRequeteDiversiteCluster = prcRang.diversify(resultatRequete, 20);
		System.out.println(resultatRequeteDiversiteCluster);
		
		System.out.println("creation measure");
		IRList irList = new IRList(query, resultatRequete);
		IRList irListDivDiss = new IRList(query, resultatRequeteDiversiteDissimilarite);
		IRList irListDivClu = new IRList(query, resultatRequeteDiversiteCluster);

		// Calcul du nombre de vrai sous thème
		Set<String> nClusters = new HashSet<String>();
		for(String doc : irList.getQuery().getClusters().keySet()){
			nClusters.add(irList.getQuery().getClusters().get(doc));
		}
		int nbSousThemes = nClusters.size();

		//System.out.println(resultatRequete);
		//System.out.println(resultatRequeteDiversite);
		PrecisionRappel precisionRappel = new PrecisionRappel(20);
		PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
		PrecisionAtN precisionAtn = new PrecisionAtN(20);
		ClusterRecall clusterRecall = new ClusterRecall(nbSousThemes, 20);
		
		System.out.println("evaluation modele");
		//List<Double> resPM = precisionMoyenne.eval(irList);
		//List<Double> resPR = precisionRappel.eval(irList);
		List<Double> resPRN = precisionAtn.eval(irList);
		List<Double> resCR = clusterRecall.eval(irList);
		List<Double> resPRNDivDiss = precisionAtn.eval(irListDivDiss);
		List<Double> resCRDivDiss = clusterRecall.eval(irListDivDiss);
		List<Double> resPRNDivClu = precisionAtn.eval(irListDivClu);
		List<Double> resCRDivClu = clusterRecall.eval(irListDivClu);
		//System.out.println("Prï¿½cision moyenne = "+resPM.get(0) + "\n" + "Prï¿½cision Rappel = " + resPR);
		System.out.println("P@20 = "+resPRN.get(0) + "\nCluster Recall = " +resCR);
		System.out.println("P@20 Diversifié par Dissimilarité = "+resPRNDivDiss.get(0) + "\nCluster Recall Diversifié par dissimilarité = " +resCRDivDiss);
		System.out.println("P@20 Diversifié par clustering = "+resPRNDivClu.get(0) + "\nCluster Recall Diversifié par clustering = " +resCRDivClu);

		
		
		
	}

}
