package diversite;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import indexation.Index;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class ClusteringKMeans extends Clustering{

	private int nbCluster, nbIter;
	
	public ClusteringKMeans(RandomAccessFile index, Index indexObjet) {
		super(index, indexObjet);
		this.nbCluster = 5;
		this.nbIter = 100;
	}

	public ClusteringKMeans(RandomAccessFile index, Index indexObjet, int nbCluster) {
		super(index, indexObjet);
		this.nbCluster = nbCluster;
	}
	
	public ClusteringKMeans(RandomAccessFile index, Index indexObjet, int nbCluster, int nbIter) {
		super(index, indexObjet);
		this.nbCluster = nbCluster;
		this.nbIter = nbIter;
	}


	@Override
	public Map<String, Integer> clustering() throws Exception{
		// Creation du classifieur
		SimpleKMeans kMeans = new SimpleKMeans();
		kMeans.setMaxIterations(nbIter);
		kMeans.setNumClusters(nbCluster);
		kMeans.setPreserveInstancesOrder(true);
		
		// On crée le sous-dataset associé
		Instances ds = createDataset();
		// On clusterise
		kMeans.buildClusterer(ds);
		/*
		Map<Integer, ArrayList<String>> clusters = new HashMap<Integer, ArrayList<String>>();
		
		for (int i = 0; i < ds.numInstances(); i++) {
			String idDoc = String.valueOf((int)ds.instance(i).weight());
			int idCluster = kMeans.clusterInstance(ds.instance(i));
			if (!clusters.containsKey(idCluster)) {
				ArrayList<String> list = new ArrayList<String>();
				clusters.put(idCluster, list);
			}
			clusters.get(kMeans.clusterInstance(ds.instance(i))).add(idDoc);
		}*/
		
		Map<String, Integer> clusters = new HashMap<String, Integer>();
		for (int i = 0; i < ds.numInstances(); i++) {
			String idDoc = String.valueOf((int)ds.instance(i).weight());
			int idCluster = kMeans.clusterInstance(ds.instance(i));
			clusters.put(idDoc, idCluster);
		}
		
		return clusters;
	}
	
	public Map<String, Integer> clustering(TreeMap<String, Double> ranking, int N) throws Exception {
		// Creation du classifieur
		SimpleKMeans kMeans = new SimpleKMeans();
		kMeans.setMaxIterations(nbIter);
		kMeans.setNumClusters(nbCluster);
		kMeans.setPreserveInstancesOrder(true);

		// On crée le sous-dataset associé
		Instances ds = createDataset(ranking, N);
		// On clusterise
		kMeans.buildClusterer(ds);


		Map<String, Integer> clusters = new HashMap<String, Integer>();
		for (int i = 0; i < ds.numInstances(); i++) {
			String idDoc = String.valueOf((int)ds.instance(i).weight());
			int idCluster = kMeans.clusterInstance(ds.instance(i));
			clusters.put(idDoc, idCluster);
		}
		
		return clusters;
	}

}
