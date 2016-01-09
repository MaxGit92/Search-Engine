package diversite;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import indexation.Index;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Clustering {
	protected RandomAccessFile index;
	protected Index indexObjet;
	protected int nbCluster;
	
	public Clustering(RandomAccessFile index, Index indexObjet, int nbCluster){
		this.index = index;
		this.indexObjet = indexObjet;
		this.nbCluster = nbCluster;
	}
	
	public double[] createSparseVector(HashMap<String, Double> tfsForDoc, Map<String, Integer> posOfStemInVector){
		double[] sparseVector = new double[posOfStemInVector.size()];
		for(String stem : tfsForDoc.keySet()){
			sparseVector[posOfStemInVector.get(stem)] = tfsForDoc.get(stem);
		}
		return sparseVector;
	}
	
	public Instances createDataset(){
		// Récupération de tous les mots du corpus (sous forme de tableau)
		Set<String> stemSet = indexObjet.getStems().keySet();
		String[] stems = stemSet.toArray(new String[stemSet.size()]);
		// Création d'une hashmap pour placer le stem dans le sparse vector facilement
		Map<String, Integer> posOfStemInVector = new HashMap<String, Integer>();
		for(int i=0; i<stems.length; i++){
			posOfStemInVector.put(stems[i], i);
		}
		// Creation du fastVector
		FastVector fv = new FastVector(); 
		for(int i=0; i<posOfStemInVector.size() ;i++){ 
		        fv.addElement(new Attribute("stem" + i)); 
		}
		
		// Creation de l'instance à retourner
		Instances data = new Instances("kmeans", fv, 0);

		for(String id : indexObjet.getDocFrom().getId()){
			HashMap<String, Double> tfsForDoc = indexObjet.getTfsForDoc(id, index);
			double[] sparseVector = createSparseVector(tfsForDoc, posOfStemInVector);
			Instance tmp = new Instance(Double.parseDouble(id), sparseVector);
			data.add(tmp);
		}
		return data;
	}
	
	public Instances createDataset(TreeMap<String, Double> ranking, int N){
		// Récupération de tous les mots du corpus (sous forme de tableau)
		Set<String> stemSet = indexObjet.getStems().keySet();
		String[] stems = stemSet.toArray(new String[stemSet.size()]);
		// Création d'une hashmap pour placer le stem dans le sparse vector facilement
		Map<String, Integer> posOfStemInVector = new HashMap<String, Integer>();
		for(int i=0; i<stems.length; i++){
			posOfStemInVector.put(stems[i], i);
		}
		// Creation du fastVector
		FastVector fv = new FastVector(); 
		for(int i=0; i<posOfStemInVector.size() ;i++){ 
		        fv.addElement(new Attribute("stem" + i)); 
		}
		
		// Creation de l'instance à retourner
		Instances data = new Instances("kmeans", fv, 0);

		// Récupération des ids des relevant (ordonnés)
		Map<String, Double> mapRanking = new HashMap<String, Double>();
		mapRanking.putAll(ranking);
		Set<String> ids = ranking.keySet();
		
		int cpt=0;
		for(String id : ids){
			if(cpt==N) break;
			HashMap<String, Double> tfsForDoc = indexObjet.getTfsForDoc(id, index);
			double[] sparseVector = createSparseVector(tfsForDoc, posOfStemInVector);
			Instance tmp = new Instance(Double.parseDouble(id), sparseVector);
			data.add(tmp);
			cpt++;
		}
		return data;
	}
	

	public int getNbCluster() {
		return nbCluster;
	}
		
	public abstract Map<String, Integer> clustering() throws Exception;
	public abstract Map<String, Integer> clustering(TreeMap<String, Double> ranking, int N) throws Exception;
}
