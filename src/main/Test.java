package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import diversite.ClusteringKMeans;
import diversite.Similarite;
import diversite.SimilariteLanguageModel;
import diversite.SimilariteVectoriel;
import indexation.Index;
import indexation.IndexMultimedia;
import indexation.Stemmer;
import indexation.TextRepresenter;
import modeles.WeighterVectoriel1;
import modeles.WeighterVectoriel5;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.tools.InstanceTools;
import weka.core.Instance;

public class test {
	public static void main(String args[]) throws ClassNotFoundException, IOException{
		
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
		
		System.out.println("Clustering");
		ClusteringKMeans kMeans = new ClusteringKMeans(index, indexObjet, 8, 50);
		Dataset[] clusters = kMeans.clustering();
		for(int i=0; i<clusters.length; i++){
			System.out.println(clusters[0]);
		}
		
		
		
	}
}
