package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import evaluation.EvalIRModel;
import evaluation.EvalMeasure;
import evaluation.EvaluationRepresentation;
import evaluation.IRList;
import evaluation.PrecisionMoyenne;
import evaluation.PrecisionRappel;
import evaluation.Query;
import evaluation.QueryParser;
import evaluation.QueryParser_CISI_CACM;
import features.Featurer;
import features.FeaturersList;
import features.KeyDQ;
import modeles.BM25;
import modeles.FeaturerModel;
import modeles.FindExtraParametersBM25;
import modeles.FindExtraParametersLanguageModel;
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
import indexation.Index;
import indexation.Parser;
import indexation.ParserCISI_CACM;
import indexation.Stemmer;
import indexation.TextRepresenter;

public class Main {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
		System.out.println("MAIN");
		
		/*
		 * PACKAGE INDEXATION
		 */
		
		/*
		// CRATION DES INDEX
		// Definition des parametres
		System.out.println("Indexation");
		String fichierCisi = "cisi";
		String fichierCacm = "cacm";
		Parser p = new ParserCISI_CACM();
		Stemmer s = new Stemmer();
		// création des index
		Index indexCisi = new Index(fichierCisi, p, s);
		Index indexCacm = new Index(fichierCacm, p, s);
		// execution de l'indexation
		indexCisi.indexation();
		indexCacm.indexation();
		// serialisation
		indexCisi.enregisterObjetIndex("cisi.ser");
		indexCacm.enregisterObjetIndex("cacm.ser");
		*/

		
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
		Weighter weighterCisi1 = new WeighterVectoriel1(indexNameCisi, invertedNameCisi, indexObjetCisi);
		Weighter weighterCisi2 = new WeighterVectoriel2(indexNameCisi, invertedNameCisi, indexObjetCisi);
		Weighter weighterCisi3 = new WeighterVectoriel3(indexNameCisi, invertedNameCisi, indexObjetCisi);
		
		Weighter weighterCacm1 = new WeighterVectoriel1(indexNameCacm, invertedNameCacm, indexObjetCacm);
		Weighter weighterCacm2 = new WeighterVectoriel2(indexNameCacm, invertedNameCacm, indexObjetCacm);
		Weighter weighterCacm3 = new WeighterVectoriel3(indexNameCacm, invertedNameCacm, indexObjetCacm);

		System.out.println("creation Modeles");
		Boolean normalized = true;
		IRmodel vectorielCisi1 = new Vectoriel(indexCisi, weighterCisi1, normalized);
		IRmodel vectorielCisi2 = new Vectoriel(indexCisi, weighterCisi2, normalized);
		IRmodel vectorielCisi3 = new Vectoriel(indexCisi, weighterCisi3, normalized);
		
		IRmodel vectorielCacm1 = new Vectoriel(indexCacm, weighterCacm1, normalized);
		IRmodel vectorielCacm2 = new Vectoriel(indexCacm, weighterCacm2, normalized);
		IRmodel vectorielCacm3 = new Vectoriel(indexCacm, weighterCacm3, normalized);

		LanguageModel languageModelCisi = new LanguageModel(indexCisi, "cisi_inverted", indexObjetCisi, (float) 0.1); // bon lambda = 0.1 !!
		BM25 bm25Cisi = new BM25(indexCisi, "cisi_inverted", indexObjetCisi, 1.8, 0.75);
		
		LanguageModel languageModelCacm = new LanguageModel(indexCacm, "cisi_inverted", indexObjetCacm, (float) 0.1);
		BM25 bm25Cacm = new BM25(indexCacm, "cisi_inverted", indexObjetCacm, 1.8, 0.75);
		
		
		
		System.out.println("creation query et queryStem");
		QueryParser_CISI_CACM queryParserCisi = new QueryParser_CISI_CACM();
		queryParserCisi.init("cisi.qry", "cisi.rel");
		
		QueryParser_CISI_CACM queryParserCacm = new QueryParser_CISI_CACM();
		queryParserCacm.init("cacm.qry", "cacm.rel");

		TextRepresenter stemmer = new Stemmer();

		System.out.println("On détermine le meilleur lambda de languageModel");
		FindExtraParametersLanguageModel feplm = new FindExtraParametersLanguageModel(languageModelCisi,
				queryParserCisi, (float) 0.5, (float) 0.1, (float) 0.2, (float) 0.01);
		float lambda = feplm.findExtraParameters();
		
		FindExtraParametersBM25 fepbm25 = new FindExtraParametersBM25(bm25Cisi,
				queryParserCisi, (float) 0.5, (float) 0.71, (float) 0.81, (float) 0.02, (float) 1, (float) 2, (float) 0.1);
		float[] bk1 = fepbm25.findExtraParameters();

		RandomAccessFile index1 = new RandomAccessFile("bestBM25", "rw");
		index1.writeBytes("lambda=" + lambda + " k1="+bk1[0]+" b="+bk1[1]);
		index1.close();
		// RESULTAT LAMBDA = 0.1 regarder entre 0.1 et 0.2
		
		// ON REDEFNIR LES MODELE DE LANGUES AVEC LES HYPER PARAMETRES OBTENUS
		languageModelCisi = new LanguageModel(indexCisi, "cisi_inverted", indexObjetCisi, lambda); // bon lambda = 0.1 !!
		bm25Cisi = new BM25(indexCisi, "cisi_inverted", indexObjetCisi, bk1[0], bk1[1]);
		
		languageModelCacm = new LanguageModel(indexCacm, "cisi_inverted", indexObjetCacm, lambda);
		bm25Cacm = new BM25(indexCacm, "cisi_inverted", indexObjetCacm, bk1[0], bk1[1]);
		
		
		
		// PARTIE RANDOMWALK
		//Map<String, HashSet<String>> graphe = indexObjet.getDocFrom().getLinksDoc();
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
		Query query = queryParserCisi.nextQuery();
		query = queryParserCisi.nextQuery();
		query = queryParserCisi.nextQuery();
		query = queryParserCisi.nextQuery();

		System.out.println(query.getId());
		
		HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());
		
		System.out.println("calcul ranking");
		TreeMap<String, Double> resultatRequete = vectorielCisi1.getRanking(queryStem);
		System.out.println(resultatRequete);
		
		
		System.out.println("creation measure");
		IRList irList = new IRList(query, resultatRequete);
		PrecisionRappel precisionRappel = new PrecisionRappel(10);
		PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
		
		System.out.println("evaluation modele");
		List<Double> res = precisionMoyenne.eval(irList);
		
		System.out.println(res);
*/
		
		/*
		System.out.println("Test de EvalIRModel");
		List<IRmodel> iRmodels = new ArrayList<IRmodel>();
		queryParser = new QueryParser_CISI_CACM();
		queryParser.init("cisi.qry", "cisi.rel");
		iRmodels.add(new Vectoriel(index, new WeighterVectoriel1(index, inverted, indexObjet), true));
		iRmodels.add(new Vectoriel(index, new WeighterVectoriel2(index, inverted, indexObjet), false));
		iRmodels.add(new Vectoriel(index, new WeighterVectoriel3(index, inverted, indexObjet), false));
		List<EvalMeasure> evalMeasrues = new ArrayList<EvalMeasure>();
		evalMeasrues.add(new PrecisionRappel(10));
		evalMeasrues.add(new PrecisionMoyenne());
		int nbQuery=5;
		int beginQuery=0;
		EvalIRModel evalIRModel = new EvalIRModel(iRmodels, evalMeasrues, queryParser, nbQuery, beginQuery);
		//evalIRModel.allEvalsForAllModels();
		System.out.println(evalIRModel.toString());
		*/
		
		
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
		
		//featurersCisi.add(featurerModelCisi1);
		//featurersCisi.add(featurerModelCisi2);
		featurersCisi.add(featurerModelCisi3);
		featurersCisi.add(featurerModelCisi4);
		featurersCisi.add(featurerModelCisi5);
		featurersCisi.add(featurerModelCisi6);
		featurersCisi.add(featurerModelCisi7);
		featurersCisi.add(featurerModelCisi8);
		featurersCisi.add(featurerModelCisi9);
		
		//featurersCacm.add(featurerModelCacm1);
		//featurersCacm.add(featurerModelCacm2);
		featurersCacm.add(featurerModelCacm3);
		featurersCacm.add(featurerModelCacm4);
		featurersCacm.add(featurerModelCacm5);
		featurersCacm.add(featurerModelCacm6);
		featurersCacm.add(featurerModelCacm7);
		featurersCacm.add(featurerModelCacm8);
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
		
		//System.out.println("Chargement features");
		//FeaturersList featurersList = (FeaturersList) Featurer.chargerFeaturer("features_cisi.ser");
		/*for(KeyDQ keyDQ : featurersList.getFeatures().keySet()){
			if(keyDQ.getIdDoc().equals("10"))
				System.out.println(keyDQ.getIdDoc() + " " + keyDQ.getIdQuery() + " " + featurersList.getFeatures().get(keyDQ).toString());
			//break;
		}*/
		//System.out.println(featurersList.getFeatures().size());
		/*
		System.out.println("Apprentissage du m�ta mod�le");
		LinearMetaModel linearMetaModel = new LinearMetaModel(index,featurersList,queryParser,indexObjet);
		linearMetaModel.fit(200, (float)0.5, (float)1e-3, (float)0.3);
		TreeMap<String,Double> resultatRequeteLinear = linearMetaModel.getRanking(query);
		TreeMap<String,Double> resultatRequeteNormal = vectoriel1.getRanking(queryStem);

		//System.out.println(resultatRequete);
		IRList irList1 = new IRList(query, resultatRequeteLinear);
		IRList irList2 = new IRList(query, resultatRequeteNormal);

		PrecisionRappel precisionRappel = new PrecisionRappel(10);
		PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
		
		System.out.println("evaluation modele NORMAL");
		List<Double> res1 = precisionRappel.eval(irList2);
		System.out.println(res1);
		
		System.out.println("evaluation modele LINEAR");
		List<Double> res2 = precisionRappel.eval(irList1);
		System.out.println(res2);
		*/
	}

}
