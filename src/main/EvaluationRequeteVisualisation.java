package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

import evaluation.IRList;
import evaluation.PrecisionMoyenne;
import evaluation.PrecisionRappel;
import evaluation.Query;
import evaluation.QueryParser;
import evaluation.QueryParser_CISI_CACM;
import features.Featurer;
import features.FeaturersList;
import indexation.Index;
import indexation.Stemmer;
import indexation.TextRepresenter;
import modeles.BM25;
import modeles.Hits;
import modeles.IRmodel;
import modeles.IRmodelFeaturer;
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

public class EvaluationRequeteVisualisation {

	private double precisionMoyenneMoyenne;
	private double[] precisionRappelMoyenne;
	private String nomModele;
	
	public EvaluationRequeteVisualisation() {
		super();
		this.precisionMoyenneMoyenne = 0;
		this.precisionRappelMoyenne = new double[9];
		for(int i = 0; i < precisionRappelMoyenne.length; i++)
			precisionRappelMoyenne[i]=0;
		this.nomModele="Unknown";
	}

	public void evaluationModele(IRmodel iRmodel, QueryParser_CISI_CACM queryParser, int init) throws IOException, InterruptedException, ExecutionException{
		if(init==1) queryParser.init("cisi.qry", "cisi.rel");
		else queryParser.init("cacm.qry", "cacm.rel");
		this.nomModele=iRmodel.toString();
		TextRepresenter stemmer = new Stemmer();
		int nbQuery = 0;
		Query query = null;
		while((query = queryParser.nextQuery()) != null){
			HashMap<String, Integer> queryStem = stemmer.getTextRepresentation(query.getText());
			TreeMap<String, Double> resultatRequete = iRmodel.getRanking(queryStem);
			IRList irList = new IRList(query, resultatRequete);
			PrecisionRappel precisionRappel = new PrecisionRappel(10);
			PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
			List<Double> resPM = precisionMoyenne.eval(irList);
			List<Double> resPR = precisionRappel.eval(irList);
			if(resPM.get(0)>=0)precisionMoyenneMoyenne+=resPM.get(0);
			for(int i = 0; i < precisionRappelMoyenne.length; i++){
				precisionRappelMoyenne[i] += resPR.get(i);
			}
			nbQuery++;
			System.out.println("query numero : "+nbQuery);
		}
		precisionMoyenneMoyenne=precisionMoyenneMoyenne*1.0/nbQuery;
		for(int i = 0; i < precisionRappelMoyenne.length; i++){
			precisionRappelMoyenne[i] = precisionRappelMoyenne[i]*1.0/nbQuery;
		}

	}
	
	public void evaluationModele(IRmodelFeaturer iRmodel, QueryParser_CISI_CACM queryParser, int init) throws IOException{
		if(init==1) queryParser.init("cisi.qry", "cisi.rel");
		else queryParser.init("cacm.qry", "cacm.rel");
		this.nomModele=iRmodel.toString();
		int nbQuery = 0;
		Query query = null;
		while((query = queryParser.nextQuery()) != null){
			TreeMap<String, Double> resultatRequete = iRmodel.getRanking(query);
			IRList irList = new IRList(query, resultatRequete);
			PrecisionRappel precisionRappel = new PrecisionRappel(10);
			PrecisionMoyenne precisionMoyenne = new PrecisionMoyenne();
			List<Double> resPM = precisionMoyenne.eval(irList);
			List<Double> resPR = precisionRappel.eval(irList);
			if(resPM.get(0)>=0)precisionMoyenneMoyenne+=resPM.get(0);
			for(int i = 0; i < precisionRappelMoyenne.length; i++){
				precisionRappelMoyenne[i] += resPR.get(i);
			}
			nbQuery++;
			System.out.println("query num�ro : "+nbQuery);
		}
		precisionMoyenneMoyenne=precisionMoyenneMoyenne*1.0/nbQuery;
		for(int i = 0; i < precisionRappelMoyenne.length; i++){
			precisionRappelMoyenne[i] += precisionRappelMoyenne[i]*1.0/nbQuery;
		}

	}
	
	public static void visualisation(List<EvaluationRequeteVisualisation> evals, String fileName){
		Plot2DPanel plot = new Plot2DPanel();

		BaseLabel title = new BaseLabel("Courbe Precision Rappel", Color.BLACK, 0.5, 1.1);
        title.setFont(new Font("Courier", Font.BOLD, 20));
        plot.addPlotable(title);
        //plot.addLegend("EAST");
		double[] x={0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
		// add a line plot to the PlotPanel
		for(EvaluationRequeteVisualisation eval : evals)
			plot.addLinePlot(eval.nomModele, x, eval.precisionRappelMoyenne);
		// change name of axes
        plot.setAxisLabels("Rappel", "Precision");
        plot.getAxis(0).setLabelPosition(0.5, -0.15);
        plot.getAxis(1).setLabelPosition(-0.15, 0.5);
        plot.setFixedBounds(0, 0.1, 0.9);
		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
        frame.setSize(1000,1000);
		frame.setContentPane(plot);
		frame.setVisible(true);
		try
        {
            BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            ImageIO.write(image,"jpeg", new File(fileName));
        }
        catch(Exception exception)
        {
            //code
        }
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
		/*
		 * PACKAGE evaluation
		 */
		System.out.println("création index");
		String indexName = "cisi_index"; // Ou "cacm_index"
		String invertedName = "cisi_inverted"; // Ou "cacm_inverted"
		String indexObjetName = "cisi.ser";
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
		Index indexObjet = Index.chargerObjetIndex(indexObjetName); // ou "cacm.ser"
		
		System.out.println("creation query et queryStem");
		QueryParser_CISI_CACM queryParser = new QueryParser_CISI_CACM();
		queryParser.init("cisi.qry", "cisi.rel");
		
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
		LanguageModel languageModel = new LanguageModel(index, "cisi_inverted", indexObjet, (float) 0.13);
		BM25 bm25 = new BM25(index, "cisi_inverted", indexObjet, 1.4, 0.8);

		// PARTIE RANDOMWALK
		float d=(float) 0.2;
		float epsilon=(float) 1e-3;
		int nbIter=300;
		PageRank pageRank = new PageRank(d, epsilon, nbIter);
		Hits hits = new Hits(nbIter);
		int n = 30;
		int k = 5;
		ModeleRandomWalk modelePageRank = new ModeleRandomWalk(index, indexObjet, pageRank, languageModel, n, k); // On peut remplacer languageModele par un autre mod�le
		ModeleRandomWalk modeleHits = new ModeleRandomWalk(index, indexObjet, hits, languageModel, n, k); // On peut remplacer bm25 par un autre mod�le

		FeaturersList featurersList = (FeaturersList) Featurer.chargerFeaturer("features_cisi.ser");
		LinearMetaModel linearMetaModel = new LinearMetaModel(index,featurersList,queryParser,indexObjet);
		linearMetaModel.fit(300, (float)0.5, (float)1e-3, (float)0.3);


		EvaluationRequeteVisualisation eval1 = new EvaluationRequeteVisualisation();
		//eval1.evaluationModele(vectoriel1, new QueryParser_CISI_CACM(), 1);
		EvaluationRequeteVisualisation eval2 = new EvaluationRequeteVisualisation();
		//eval2.evaluationModele(vectoriel2, new QueryParser_CISI_CACM(), 1);
		EvaluationRequeteVisualisation eval3 = new EvaluationRequeteVisualisation();
		//eval3.evaluationModele(vectoriel3, new QueryParser_CISI_CACM(), 1);
		EvaluationRequeteVisualisation eval4 = new EvaluationRequeteVisualisation();
		//eval4.evaluationModele(vectoriel4, new QueryParser_CISI_CACM(), 1);
		EvaluationRequeteVisualisation eval5 = new EvaluationRequeteVisualisation();
		//eval5.evaluationModele(vectoriel5, new QueryParser_CISI_CACM(), 1);
		
		EvaluationRequeteVisualisation evalLM = new EvaluationRequeteVisualisation();
		//evalLM.evaluationModele(languageModel, new QueryParser_CISI_CACM(), 1);
		EvaluationRequeteVisualisation evalBM25 = new EvaluationRequeteVisualisation();
		//evalBM25.evaluationModele(bm25, new QueryParser_CISI_CACM(), 1);
		
		EvaluationRequeteVisualisation evalPR = new EvaluationRequeteVisualisation();
		//evalPR.evaluationModele(modelePageRank, new QueryParser_CISI_CACM(), 1);
		EvaluationRequeteVisualisation evalHits = new EvaluationRequeteVisualisation();
		//evalHits.evaluationModele(modeleHits, new QueryParser_CISI_CACM(), 1);
		
		EvaluationRequeteVisualisation evalLMM = new EvaluationRequeteVisualisation();
		evalLMM.evaluationModele(linearMetaModel, new QueryParser_CISI_CACM(), 1);
		
		
		
		List<EvaluationRequeteVisualisation> evalsVect = new ArrayList<EvaluationRequeteVisualisation>();
		evalsVect.add(eval1);
		evalsVect.add(eval2);
		evalsVect.add(eval3);
		evalsVect.add(eval4);
		evalsVect.add(eval5);
		visualisation(evalsVect, "precRapVect");
		RandomAccessFile file1 = new RandomAccessFile("precMoyVect", "rw");
		for(EvaluationRequeteVisualisation eval : evalsVect)
			file1.writeBytes(eval.nomModele + " : " + eval.precisionMoyenneMoyenne + "\n");
		file1.close();
		
		List<EvaluationRequeteVisualisation> evalsLang = new ArrayList<EvaluationRequeteVisualisation>();
		evalsLang.add(evalLM);
		evalsLang.add(evalBM25);
		visualisation(evalsLang, "precRapLang");
		RandomAccessFile file2 = new RandomAccessFile("precMoyLang", "rw");
		for(EvaluationRequeteVisualisation eval : evalsLang)
			file2.writeBytes(eval.nomModele + " : " + eval.precisionMoyenneMoyenne + "\n");
		file2.close();
		
		List<EvaluationRequeteVisualisation> evalsRandWalk = new ArrayList<EvaluationRequeteVisualisation>();
		evalsRandWalk.add(evalPR);
		evalsRandWalk.add(evalHits);
		visualisation(evalsRandWalk, "precRapRandWalk");
		RandomAccessFile file3 = new RandomAccessFile("precMoyRandWalk", "rw");
		for(EvaluationRequeteVisualisation eval : evalsRandWalk)
			file3.writeBytes(eval.nomModele + " : " + eval.precisionMoyenneMoyenne + "\n");
		file3.close();
		
		List<EvaluationRequeteVisualisation> evalsLMM = new ArrayList<EvaluationRequeteVisualisation>();
		evalsLMM.add(evalLMM);
		visualisation(evalsLMM, "precRapLMM");
		RandomAccessFile file4 = new RandomAccessFile("precMoyLMM", "rw");
		for(EvaluationRequeteVisualisation eval : evalsLMM)
			file4.writeBytes(eval.nomModele + " : " + eval.precisionMoyenneMoyenne + "\n");
		file4.close();
	}
	
}
