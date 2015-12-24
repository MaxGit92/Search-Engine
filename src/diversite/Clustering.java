package diversite;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import indexation.Index;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.SparseInstance;

public abstract class Clustering {
	private RandomAccessFile index;
	private Index indexObjet;
	
	public Clustering(RandomAccessFile index, Index indexObjet){
		this.index = index;
		this.indexObjet = indexObjet;
	}
	
	public double[] createSparseVector(HashMap<String, Double> tfsForDoc, Map<String, Integer> posOfStemInVector){
		double[] sparseVector = new double[posOfStemInVector.size()];
		for(String stem : tfsForDoc.keySet()){
			sparseVector[posOfStemInVector.get(stem)] = tfsForDoc.get(stem);
		}
		return sparseVector;
	}
	
	public Dataset createDataset(){
		// Création du dataset à retourner
		Dataset dataset = new DefaultDataset();
		
		// Récupération de tous les mots du corpus (sous forme de tableau)
		Set<String> stemSet = indexObjet.getStems().keySet();
		String[] stems = stemSet.toArray(new String[stemSet.size()]);
		// Création d'une hashmap pour placer le stem dans le sparse vector facilement
		Map<String, Integer> posOfStemInVector = new HashMap<String, Integer>();
		for(int i=0; i<stems.length; i++){
			posOfStemInVector.put(stems[i], i);
		}
		
		for(String id : indexObjet.getDocFrom().getId()){
			HashMap<String, Double> tfsForDoc = indexObjet.getTfsForDoc(id, index);
			SparseInstance instance = new SparseInstance(createSparseVector(tfsForDoc, posOfStemInVector));
			dataset.add(instance);
		}
		return dataset;
	}
	
	public abstract Dataset[] clustering();
}
