package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import diversite.ClusteringKMeans;
import diversite.Similarite;
import diversite.SimilariteLanguageModel;
import diversite.SimilariteVectoriel;
import evaluation.Query;
import evaluation.QueryParser;
import evaluation.QueryParser_Multimedia;
import indexation.Index;
import indexation.IndexMultimedia;
import indexation.Stemmer;
import indexation.TextRepresenter;
import modeles.WeighterVectoriel1;
import modeles.WeighterVectoriel5;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

public class test {
	public static void main(String args[]) throws Exception{
		
		System.out.println("création index");
		String indexName = "easy235_text_index";
		String invertedName = "easy235_text_inverted";
		String relevantName = "easy235_gt.txt";
		String queryName = "easy235_query.txt";
		
		RandomAccessFile index = new RandomAccessFile(indexName, "r");
		RandomAccessFile inverted = new RandomAccessFile(invertedName, "r");
		Index indexObjet = IndexMultimedia.chargerObjetIndex("easy235.ser");
		
//		String id1 = "37353";
//		String id2 = "37368";
//		
//		Similarite similarite1 = new SimilariteLanguageModel(indexObjet, index, inverted);
//		Similarite similarite2 = new SimilariteVectoriel(indexObjet, index, inverted, new WeighterVectoriel1(indexName, invertedName, indexObjet));
//
//		System.out.println(similarite1.similarite(id1, id2));
//		System.out.println(similarite1.similarite(id2, id1));
//
//		
//		id1 = "37353";
//		id2 = "19259";
//		
//		similarite1 = new SimilariteLanguageModel(indexObjet, index, inverted);
//		similarite2 = new SimilariteVectoriel(indexObjet, index, inverted, new WeighterVectoriel1(indexName, invertedName, indexObjet));
//
//		System.out.println(similarite1.similarite(id1, id2));
//		System.out.println(similarite1.similarite(id2, id2));
//
//
//		double[][] dataTest = {{0,1},{1,0},{10,10},{11,10},
//		{2,1},{1,2},{12,12},{12,10},
//				{3,1},{1,4},{12,11},{14,19}};
//		Dataset dataset = new DefaultDataset();
//		for(int i=0; i<dataTest.length; i++){
//			DenseInstance instance = new DenseInstance(dataTest[i]);
//			dataset.add(instance);
//		}
//		
//		KMeans kmeans = new KMeans(2, 100);
//		
//		Dataset[] res = kmeans.cluster(dataset);
//		System.out.println(res[0]);
//		System.out.println(res[1]);
//		
//		System.out.println("Clustering");
//		ClusteringKMeans kMeans = new ClusteringKMeans(index, indexObjet, 8, 50);
//		Dataset[] clusters = kMeans.clustering();
//		for(int i=0; i<clusters.length; i++){
//			System.out.println(clusters[0]);
//		}
		
		
		
		
		ClusteringKMeans kMeans = new ClusteringKMeans(index, indexObjet, 8, 100);
		Map<Integer, ArrayList<String>> clusters = kMeans.clustering();
		System.out.println(clusters);
		
		
		/* POUR TESTER LE CLUSTERING (A REVOIR) */
//		RandomAccessFile cl = new RandomAccessFile("easy235_gt.txt", "r");
//
//		
//		Map<Integer, ArrayList<String>> clusters2 = new HashMap<Integer, ArrayList<String>>();
//		String l="";
//		while((l = cl.readLine())!=null){
//			String[] split = l.split(" ");
//			if(clusters2.containsKey(Integer.parseInt(split[3]))){
//				clusters2.get(Integer.parseInt(split[3])).add(split[1]);
//				continue;
//			}
//			clusters2.put(Integer.parseInt(split[3]), new ArrayList<String>());
//			clusters2.get(Integer.parseInt(split[3])).add(split[1]);
//		}
//		cl.close();
//		int cptCorr=0;
//		int cptCorrMax=Integer.MIN_VALUE;
//		ArrayList<Double> cptCorrList = new ArrayList<Double>();
//		for(Integer i : clusters.keySet()){
//			cptCorrMax=Integer.MIN_VALUE;
//			for(Integer j : clusters2.keySet()){
//				cptCorr=0;
//				for(int k=0; k<clusters2.get(j).size(); k++){
//					if(clusters.get(i).contains(clusters2.get(j).get(k)))
//						cptCorr++;
//					if(cptCorr>cptCorrMax) cptCorrMax=cptCorr;
//				}
//			}
//			cptCorrList.add((double) cptCorrMax);
//		}
//		System.out.println(cptCorrList);
//		
		
	}
}
