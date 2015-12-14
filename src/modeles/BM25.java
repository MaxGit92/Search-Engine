package modeles;

import indexation.Index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BM25 extends IRmodel{

	private Index indexObjet;
	private RandomAccessFile inverted;
	private String invertedName; // Obligatoire pour le multithread
	private double k1;
	private double b;
	
	public BM25(RandomAccessFile index, String invertedName,
			Index indexObjet, double k1, double b) {
		super(index);
		this.invertedName = invertedName;
		this.indexObjet = indexObjet;
		try {
			this.inverted = new RandomAccessFile(invertedName, "r");
		} catch (FileNotFoundException e) {
			this.inverted=null;
			e.printStackTrace();
		}
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
	
	/*
	 * Ancienne m�thode
	 
	@Override
	public HashMap<String, Double> getScores(HashMap<String, Integer> query)
			throws IOException {
		HashMap<String, Double> res = new HashMap<String, Double>();
		Double Lmoy = calculMoyenneMotsCorpus();
		this.index.seek(0);
		String ligne;
		try {
			while((ligne = this.index.readLine()) != null){
				String idDoc = ligne.split(":")[0];
				Double scoreDoc = (double) 0;
				if(Integer.parseInt(idDoc)%100==0)System.out.println(idDoc);
				for(String stem : query.keySet()){
					long tf = indexObjet.getTfForStem(stem, idDoc, inverted);
					long Ld = indexObjet.getDocFrom().getNbMots().get(idDoc);
					scoreDoc+=idfPrime(stem,inverted)*(((k1+1)*tf)/(k1*((1-b)+(b*Ld)/Lmoy)+tf));
				}
				res.put(idDoc, scoreDoc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	*/
	/**
	 * Calcul du score selon l'algorithme Okapi
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	
	@Override
	public HashMap<String, Double> getScores(HashMap<String, Integer> query)
			throws IOException, InterruptedException, ExecutionException {
		int nbThreads = Runtime.getRuntime().availableProcessors()+1;
		
		ExecutorService pool = Executors.newFixedThreadPool(nbThreads);
		Future<Double> resultat[] = new Future[indexObjet.getDocFrom().getId().size()];
		
		HashMap<String, Double> res = new HashMap<String, Double>();
		Double Lmoy = calculMoyenneMotsCorpus();
		this.index.seek(0);
		String ligne;
		ArrayList<String> idsDoc = new ArrayList<String>();
		int nbDocRead = 0;
		try {
			while((ligne = this.index.readLine()) != null){
				final String idDoc = ligne.split(":")[0];
				idsDoc.add(idDoc);
				//if(Integer.parseInt(idDoc)%100==0)System.out.println(idDoc);
				
				resultat[nbDocRead] = pool.submit(new Callable<Double>(){
					@Override
					public Double call() throws Exception{
						Double scoreDoc = 0.0;
						RandomAccessFile r = new RandomAccessFile(invertedName, "r");
						for(String stem : query.keySet()){
							long tf = indexObjet.getTfForStem(stem, idDoc, r);
							long Ld = indexObjet.getDocFrom().getNbMots().get(idDoc);
							scoreDoc+=idfPrime(stem,r)*(((k1+1)*tf)/(k1*((1-b)+(b*Ld)/Lmoy)+tf));
						}
						r.close();
						return scoreDoc;
					}
				});
				nbDocRead++;
			}
			for(int i=0; i<indexObjet.getDocFrom().getId().size(); i++){
				res.put(idsDoc.get(i), resultat[i].get());
			}
			pool.shutdown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public void setK1(double k1) {
		this.k1 = k1;
	}

	public void setB(double b) {
		this.b = b;
	}
	
	@Override
	public String toString(){
		return "Modele BM25";
	}
	
	
}
