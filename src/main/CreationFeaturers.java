package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import modeles.WeighterVectoriel4;
import modeles.WeighterVectoriel5;

public class CreationFeaturers {
	public static void main(String args[]) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException{
		// ATTENTION CETTE EXECUTION PREND DU TEMPS
		
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("création index");
		String indexNameCisi = "cisi_index";
		String invertedNameCisi = "cisi_inverted";
		
		String indexNameCacm = "cacm_index";
		String invertedNameCacm = "cacm_inverted";
		
		RandomAccessFile indexCisi = new RandomAccessFile(indexNameCisi, "r");
		RandomAccessFile invertedCisi = new RandomAccessFile(invertedNameCisi, "r");
		Index indexObjetCisi = Index.chargerObjetIndex("cisi.ser");
		
		RandomAccessFile indexCacm = new RandomAccessFile(indexNameCacm, "r");
		RandomAccessFile invertedCacm = new RandomAccessFile(invertedNameCacm, "r");
		Index indexObjetCacm = Index.chargerObjetIndex("cacm.ser");
		
		System.out.println("creation weighter");
		Weighter weighterCisi1 = new WeighterVectoriel2(indexNameCisi, invertedNameCisi, indexObjetCisi);
		Weighter weighterCisi2 = new WeighterVectoriel3(indexNameCisi, invertedNameCisi, indexObjetCisi);
		Weighter weighterCisi3 = new WeighterVectoriel4(indexNameCisi, invertedNameCisi, indexObjetCisi);
		
		Weighter weighterCacm1 = new WeighterVectoriel2(indexNameCacm, invertedNameCacm, indexObjetCacm);
		Weighter weighterCacm2 = new WeighterVectoriel3(indexNameCacm, invertedNameCacm, indexObjetCacm);
		Weighter weighterCacm3 = new WeighterVectoriel4(indexNameCacm, invertedNameCacm, indexObjetCacm);

		System.out.println("creation Modeles");
		Boolean normalized = true;
		IRmodel vectorielCisi1 = new Vectoriel(indexCisi, weighterCisi1, normalized);
		IRmodel vectorielCisi2 = new Vectoriel(indexCisi, weighterCisi2, normalized);
		IRmodel vectorielCisi3 = new Vectoriel(indexCisi, weighterCisi3, normalized);
		
		IRmodel vectorielCacm1 = new Vectoriel(indexCacm, weighterCacm1, normalized);
		IRmodel vectorielCacm2 = new Vectoriel(indexCacm, weighterCacm2, normalized);
		IRmodel vectorielCacm3 = new Vectoriel(indexCacm, weighterCacm3, normalized);

		LanguageModel languageModelCisi = new LanguageModel(indexCisi, "cisi_inverted", indexObjetCisi, (float) 0.13); // bon lambda = 0.1 !!
		BM25 bm25Cisi = new BM25(indexCisi, "cisi_inverted", indexObjetCisi, 1.4, 0.8);
		
		LanguageModel languageModelCacm = new LanguageModel(indexCacm, "cacm_inverted", indexObjetCacm, (float) 0.13);
		BM25 bm25Cacm = new BM25(indexCacm, "cacm_inverted", indexObjetCacm, 1.4, 0.8);
		
		System.out.println("creation query et queryStem");
		QueryParser_CISI_CACM queryParserCisi = new QueryParser_CISI_CACM();
		queryParserCisi.init("cisi.qry", "cisi.rel");
		
		QueryParser_CISI_CACM queryParserCacm = new QueryParser_CISI_CACM();
		queryParserCacm.init("cacm.qry", "cacm.rel");

		TextRepresenter stemmer = new Stemmer();
	
		
		// PARTIE RANDOMWALK
		float d=(float) 0.2;
		float epsilon=(float) 1e-3;
		int nbIter=300;
		PageRank pageRank = new PageRank(d, epsilon, nbIter);
		Hits hits = new Hits(nbIter);
		int n = 30;
		int k = 5;
		ModeleRandomWalk modelePageRankCisi1 = new ModeleRandomWalk(indexCisi, indexObjetCisi, pageRank, languageModelCisi, n, k);
		ModeleRandomWalk modelePageRankCisi2 = new ModeleRandomWalk(indexCisi, indexObjetCisi, pageRank, bm25Cisi, n, k);
		ModeleRandomWalk modeleHitsCisi1 = new ModeleRandomWalk(indexCisi, indexObjetCisi, hits, languageModelCisi, n, k);
		ModeleRandomWalk modeleHitsCisi2 = new ModeleRandomWalk(indexCisi, indexObjetCisi, hits, bm25Cisi, n, k);
		
		ModeleRandomWalk modelePageRankCacm1 = new ModeleRandomWalk(indexCacm, indexObjetCacm, pageRank, languageModelCacm, n, k);
		ModeleRandomWalk modelePageRankCacm2 = new ModeleRandomWalk(indexCacm, indexObjetCacm, pageRank, bm25Cacm, n, k);
		ModeleRandomWalk modeleHitsCacm1 = new ModeleRandomWalk(indexCacm, indexObjetCacm, hits, languageModelCacm, n, k);
		ModeleRandomWalk modeleHitsCacm2 = new ModeleRandomWalk(indexCacm, indexObjetCacm, hits, bm25Cacm, n, k);

		
		/*
		 * META MODEL
		 */

		System.out.println("test MetaModel");
		System.out.println("Initialisation des featurers");
		FeaturerModel featurerModelCisi1 = new FeaturerModel(indexCisi, "features_cisi.ser", vectorielCisi1);
		FeaturerModel featurerModelCisi2 = new FeaturerModel(indexCisi, "features_cisi.ser", vectorielCisi2);
		FeaturerModel featurerModelCisi3 = new FeaturerModel(indexCisi, "features_cisi.ser", vectorielCisi3);
		FeaturerModel featurerModelCisi4 = new FeaturerModel(indexCisi, "features_cisi.ser", languageModelCisi);
		FeaturerModel featurerModelCisi5 = new FeaturerModel(indexCisi, "features_cisi.ser", bm25Cisi);
		FeaturerModel featurerModelCisi6 = new FeaturerModel(indexCisi, "features_cisi.ser", modelePageRankCisi1);
		FeaturerModel featurerModelCisi7 = new FeaturerModel(indexCisi, "features_cisi.ser", modeleHitsCisi1);
		FeaturerModel featurerModelCisi8 = new FeaturerModel(indexCisi, "features_cisi.ser", modelePageRankCisi2);
		FeaturerModel featurerModelCisi9 = new FeaturerModel(indexCisi, "features_cisi.ser", modeleHitsCisi2);

		FeaturerModel featurerModelCacm1 = new FeaturerModel(indexCacm, "features_cacm.ser", vectorielCacm1);
		FeaturerModel featurerModelCacm2 = new FeaturerModel(indexCacm, "features_cacm.ser", vectorielCacm2);
		FeaturerModel featurerModelCacm3 = new FeaturerModel(indexCacm, "features_cacm.ser", vectorielCacm3);
		FeaturerModel featurerModelCacm4 = new FeaturerModel(indexCacm, "features_cacm.ser", languageModelCacm);
		FeaturerModel featurerModelCacm5 = new FeaturerModel(indexCacm, "features_cacm.ser", bm25Cacm);
		FeaturerModel featurerModelCacm6 = new FeaturerModel(indexCacm, "features_cacm.ser", modelePageRankCacm1);
		FeaturerModel featurerModelCacm7 = new FeaturerModel(indexCacm, "features_cacm.ser", modeleHitsCacm1);
		FeaturerModel featurerModelCacm8 = new FeaturerModel(indexCacm, "features_cacm.ser", modelePageRankCacm2);
		FeaturerModel featurerModelCacm9 = new FeaturerModel(indexCacm, "features_cacm.ser", modeleHitsCacm2);

		
		System.out.println("Initialisation de la liste de featurer");
		List<Featurer> featurersCisi = new ArrayList<Featurer>();
		List<Featurer> featurersCacm = new ArrayList<Featurer>();
		
		featurersCisi.add(featurerModelCisi1);
		featurersCisi.add(featurerModelCisi2);
		featurersCisi.add(featurerModelCisi3);
		featurersCisi.add(featurerModelCisi4);
		featurersCisi.add(featurerModelCisi5);
		featurersCisi.add(featurerModelCisi6);
		featurersCisi.add(featurerModelCisi7);
		featurersCisi.add(featurerModelCisi8);
		featurersCisi.add(featurerModelCisi9);
		
		featurersCacm.add(featurerModelCacm1);
		featurersCacm.add(featurerModelCacm2);
		featurersCacm.add(featurerModelCacm3);
		featurersCacm.add(featurerModelCacm4);
		featurersCacm.add(featurerModelCacm5);
		featurersCacm.add(featurerModelCacm6);
		//featurersCacm.add(featurerModelCacm7);
		//featurersCacm.add(featurerModelCacm8);
		featurersCacm.add(featurerModelCacm9);
		
		System.out.println("Creation du featurersList");
		FeaturersList featurersListCisi = new FeaturersList(indexCisi, "features_cisi.ser", featurersCisi);
		FeaturersList featurersListCacm = new FeaturersList(indexCacm, "features_cacm.ser", featurersCacm);
		
		queryParserCisi = new QueryParser_CISI_CACM();
		queryParserCisi.init("cisi.qry", "cisi.rel");
		
		queryParserCacm = new QueryParser_CISI_CACM();
		queryParserCacm.init("cacm.qry", "cacm.rel");
		
		List<Query> queriesCisi = new ArrayList<Query>();
		Query qCisi;
		while((qCisi = queryParserCisi.nextQuery())!=null)
			queriesCisi.add(qCisi);
		List<Query> queriesCacm = new ArrayList<Query>();
		Query qCacm;
		while((qCacm = queryParserCacm.nextQuery())!=null)
			queriesCacm.add(qCacm);
		
		featurersListCisi.setListeFeatures(queriesCisi);
		featurersListCisi.enregisterFeaturer();
		
		featurersListCacm.setListeFeatures(queriesCacm);
		featurersListCacm.enregisterFeaturer();
		
		
	}
}
