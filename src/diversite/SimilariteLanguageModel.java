package diversite;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import evaluation.Query;
import indexation.Index;

public class SimilariteLanguageModel extends Similarite{

	private float lambda;
	
	public SimilariteLanguageModel(Index indexObjet, RandomAccessFile index, RandomAccessFile inverted) {
		super(indexObjet, index, inverted);
		this.lambda = (float) 0.3;
	}
	
	public SimilariteLanguageModel(Index indexObjet, RandomAccessFile index, RandomAccessFile inverted, float lambda) {
		super(indexObjet, index, inverted);
		this.lambda = lambda;
	}

	
	/**
	 * probabilit� du stem sur le document idDoc
	 * @param stem
	 * @param idDoc
	 * @return
	 * @throws IOException
	 */
	public double pmd(String stem, String idDoc, RandomAccessFile inverted) throws IOException{
		return indexObjet.getTfForStem(stem, idDoc, inverted)*1.0/indexObjet.getDocFrom().getNbMots().get(idDoc);
	}

	/**
	 * probabilit� du stem sur l'ensemble du corpus
	 * @param stem
	 * @param nbMotsCorpus
	 * @return
	 * @throws IOException
	 */
	public double pmc(String stem, long nbMotsCorpus, RandomAccessFile inverted) throws IOException{
		//inverted.seek(0);
		HashMap<String, Double> tfsStem = indexObjet.getTfsForStem(stem, inverted);
		Double occurence = 0.0;
		for(String key : tfsStem.keySet()){
			occurence += tfsStem.get(key);
		}
		return occurence/nbMotsCorpus;
	}
	
	/**
	 * Calcul du nombre de mots du corpus
	 * @return
	 */
	public long calculMotsCorpus(){
		long nbMots = 0;
		for(String idDoc : indexObjet.getDocFrom().getId()){
			nbMots += indexObjet.getDocFrom().getNbMots().get(idDoc);
		}
		return nbMots;
	}
	
	@Override
	public double similarite(String idDoc1, String idDoc2) throws IOException {
				
		long nbMotsCorpus = calculMotsCorpus();
		double posDoc1 = indexObjet.getDocFrom().getPosInIndex().get(idDoc1);
		double posDoc2 = indexObjet.getDocFrom().getPosInIndex().get(idDoc2);
		index.seek((long) posDoc1);
		String ligneDoc1 = index.readLine();
		index.seek((long) posDoc2);
		String ligneDoc2 = index.readLine();
		String[] s1 = ligneDoc1.split(":")[1].split(";");
		String[] s2 = ligneDoc2.split(":")[1].split(";");
		Map<String, Integer> stemsDoc1 = new HashMap<String, Integer>();
		Map<String, Integer> stemsDoc2 = new HashMap<String, Integer>();
		for(int i=0; i<s1.length; i++){
			String ss[] = s1[i].split("-");
			stemsDoc1.put(ss[0],Integer.parseInt(ss[1]));
		}
		for(int i=0; i<s2.length; i++){
			String ss[] = s2[i].split("-");
			stemsDoc2.put(ss[0],Integer.parseInt(ss[1]));
		}
		Double score1 = 0.0;
		Double score2 = 0.0;
		for(String stem : stemsDoc1.keySet()){
			//System.out.println("pmd = "+pmd(stem,idDoc1,inverted) + " pmc = " + pmc(stem, nbMotsCorpus,inverted));
			if((score1 = lambda*pmd(stem,idDoc2,inverted) + (1-lambda)*pmc(stem, nbMotsCorpus,inverted))==0) continue;
			score1+=(double)stemsDoc1.get(stem)*Math.log(score1);
		}
		for(String stem : stemsDoc2.keySet()){
			if((score2 = lambda*pmd(stem,idDoc1,inverted) + (1-lambda)*pmc(stem, nbMotsCorpus,inverted))==0) continue;
			score2+=(double)stemsDoc2.get(stem)*Math.log(score2);
		}
		return score1+score2 ;
	}
	
	@Override
	public double[][] allScores() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getLambda() {
		return lambda;
	}

	public void setLambda(float lambda) {
		this.lambda = lambda;
	}

	

}
