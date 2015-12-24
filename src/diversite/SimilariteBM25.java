package diversite;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import indexation.Index;

public class SimilariteBM25 extends Similarite{

	float k1;
	float b;
	
	public SimilariteBM25(Index indexObjet, RandomAccessFile index, RandomAccessFile inverted) {
		super(indexObjet, index, inverted);
		this.k1 = (float)1.5;
		this.b = (float) 0.75;
	}
	
	public SimilariteBM25(Index indexObjet, RandomAccessFile index, RandomAccessFile inverted, float k1, float b) {
		super(indexObjet, index, inverted);
		this.k1 = k1;
		this.b = b;
	}

	/**
	 * Calcul de la probabilit� idf
	 * @param stem
	 * @return
	 */
	public double idfPrime(String stem, RandomAccessFile inverted){
		long df = indexObjet.getTfsForStem(stem, inverted).size();
		long N = indexObjet.getDocFrom().getId().size();
		return Math.max(0, Math.log((N-df+0.5)/(df+0.5)));
	}
	
	/**
	 * Calcul de la moyenne des mots des documents du corpus
	 * @return
	 */
	public Double calculMoyenneMotsCorpus(){
		long nbMots = 0;
		for(String idDoc : indexObjet.getDocFrom().getId()){
			nbMots += indexObjet.getDocFrom().getNbMots().get(idDoc);
		}
		return nbMots*1.0/indexObjet.getDocFrom().getId().size();
	}
	
	@Override
	public double similarite(String idDoc1, String idDoc2) throws IOException {
		Double Lmoy = calculMoyenneMotsCorpus();
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
		double score1=0, score2=0;
		for(String stem : stemsDoc1.keySet()){
			long tf = indexObjet.getTfForStem(stem, idDoc2, inverted);
			long Ld = indexObjet.getDocFrom().getNbMots().get(idDoc2);
			score1+=idfPrime(stem,inverted)*(((k1+1)*tf)/(k1*((1-b)+(b*Ld)/Lmoy)+tf));
		}
		for(String stem : stemsDoc2.keySet()){
			long tf = indexObjet.getTfForStem(stem, idDoc1, inverted);
			long Ld = indexObjet.getDocFrom().getNbMots().get(idDoc1);
			score2+=idfPrime(stem,inverted)*(((k1+1)*tf)/(k1*((1-b)+(b*Ld)/Lmoy)+tf));
		}
		return score1+score2;
	}

	@Override
	public double[][] allScores() {
		// TODO Auto-generated method stub
		return null;
	}

}
