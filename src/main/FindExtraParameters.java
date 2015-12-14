package main;

import java.io.FileNotFoundException;
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
import indexation.Index;
import indexation.Stemmer;
import indexation.TextRepresenter;
import modeles.BM25;
import modeles.FeaturerModel;
import modeles.FindExtraParametersBM25;
import modeles.FindExtraParametersLanguageModel;
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

public class FindExtraParameters {
	public static void main(String args[]) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException{
		System.out.println("MAIN");
		
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("cr√©ation index");
		String indexName = "cisi_index"; // Ou "cacm_index"
		String invertedName = "cisi_inverted"; // Ou "cacm_inverted"
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
		Index indexObjet = Index.chargerObjetIndex("cisi.ser"); // ou "cacm.ser"

		QueryParser_CISI_CACM queryParser = new QueryParser_CISI_CACM();
		queryParser.init("cisi.qry", "cisi.rel"); // Ou "cacm.qry, "cacm.rel"
		
		LanguageModel languageModelCisi = new LanguageModel(index, invertedName, indexObjet, (float) 0.1); // bon lambda = 0.1 !!
		BM25 bm25Cisi = new BM25(index, invertedName, indexObjet, 1.8, 0.75);

		System.out.println("On d√©termine le meilleur lambda de languageModel");
		FindExtraParametersLanguageModel feplm = new FindExtraParametersLanguageModel(languageModelCisi,
				queryParser, (float) 0.5, (float) 0.1, (float) 1, (float) 0.1);
		float lambda = feplm.findExtraParameters();
		
		System.out.println("On d√©termine les meilleurs k1 et b de bm25");
		FindExtraParametersBM25 fepbm25 = new FindExtraParametersBM25(bm25Cisi,
				queryParser, (float) 0.5, (float) 0.71, (float) 0.81, (float) 0.02, (float) 1, (float) 2, (float) 0.1);
		float[] bk1 = fepbm25.findExtraParameters();

		// On enregistre les resultats dans un fichier Ètant donnÈ le temps d'apprentissage
		RandomAccessFile index1 = new RandomAccessFile("best", "rw");
		index1.writeBytes("lambda=" + lambda + " k1="+bk1[0]+" b="+bk1[1]);
		index1.close();
		
	}
}
