package diversite;

import java.io.RandomAccessFile;

import indexation.Index;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;

public class ClusteringKMeans extends Clustering{

	private int nbCluster, nbIter;
	private DistanceMeasure dm;
	
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
	public Dataset[] clustering() {
		KMeans kMeans = new KMeans(nbCluster, nbIter);
		Dataset dataset = createDataset();
		System.out.println("calcul des clusters");
		return kMeans.cluster(dataset);
	}

}
