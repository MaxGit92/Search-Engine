package diversite;

import java.io.IOException;
import java.io.RandomAccessFile;

import evaluation.Query;
import indexation.Index;

public abstract class Similarite {

	Index indexObjet;
	RandomAccessFile index;
	RandomAccessFile inverted;
	
	
	public Similarite(Index indexObjet, RandomAccessFile index, RandomAccessFile inverted) {
		super();
		this.indexObjet = indexObjet;
		this.index = index;
		this.inverted = inverted;
	}

	/**
	 * Calcule la similarité max avec les documents pivot
	 * @param idDocs
	 * @param i
	 * @return
	 * @throws IOException 
	 */
	public double similariteMax(String[] idDocs, int begin) throws IOException{
		double maxSim = Double.MIN_VALUE;
		for(int i = begin-1; i>=0; i--){
			double sim = similarite(idDocs[begin], idDocs[i]);
			if(sim > maxSim) maxSim=sim;
		}
		return maxSim;
	}
	
	/**
	 * Renvoie la somme des similarite de du meilleur document jusqu'au ieme document
	 * @param idDocs
	 * @param begin
	 * @return
	 * @throws IOException
	 */
	public double similariteSum(String[] idDocs, int begin) throws IOException {
		double sumSim=0;
		for(int i = begin-1; i>=0; i--){
			sumSim += similarite(idDocs[begin], idDocs[i]);
		}
		return sumSim;
	}
	
	public abstract double similarite(String idDoc1, String idDoc2) throws IOException;
	
	public abstract double[][] allScores();

	
}
