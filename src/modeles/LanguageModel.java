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

public class LanguageModel extends IRmodel{
	private float lambda;
	private RandomAccessFile inverted;
	private Index indexObjet;
	private String invertedName; // Nous sommes contraint de donner le nom de l'index afin de cr�er des randomAccessFile pour chaque thead
	public LanguageModel(RandomAccessFile index, String invertedName, Index indexObjet, float lambda) {
		super(index);
		this.invertedName = invertedName;
		try {
			this.inverted = new RandomAccessFile(invertedName, "r");
		} catch (FileNotFoundException e) {
			this.inverted=null;
			e.printStackTrace();
		}
		this.indexObjet = indexObjet;
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

	public float getLambda() {
		return lambda;
	}

	public void setLambda(float lambda) {
		this.lambda = lambda;
	}

	/*
	 * Ancienne m�thode sans parall�lisation
	 * 
	@Override
	public HashMap<String, Double> getScores(HashMap<String, Integer> query)
			throws IOException {
		HashMap<String, Double> res = new HashMap<String, Double>();
		long nbMotsCorpus = calculMotsCorpus();
		this.index.seek(0);
		String ligne;
		Double scoreDoc;
		try {
			while((ligne = this.index.readLine()) != null){
				String idDoc = ligne.split(":")[0];
				scoreDoc = (double) 0;
				if(Integer.parseInt(idDoc)%100==0)System.out.println(idDoc);
				for(String stem : query.keySet()){
					scoreDoc+=(double)query.get(stem)*Math.log(lambda*pmd(stem,idDoc,this.inverted) + (1-lambda)*pmc(stem, nbMotsCorpus, this.inverted));
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
	@Override
	public HashMap<String, Double> getScores(HashMap<String, Integer> query)
			throws IOException, InterruptedException, ExecutionException {
		int nbThreads = Runtime.getRuntime().availableProcessors()+1;
		
		ExecutorService pool = Executors.newFixedThreadPool(nbThreads);
		Future<Double> resultat[] = new Future[indexObjet.getDocFrom().getId().size()];
		
		HashMap<String, Double> res = new HashMap<String, Double>();
		long nbMotsCorpus = calculMotsCorpus();
		this.index.seek(0);
		String ligne;
		ArrayList<String> idsDoc = new ArrayList<String>();
		int nbDocRead = 0;
		try {
			while((ligne = this.index.readLine()) != null){
				final String idDoc = ligne.split(":")[0];
				idsDoc.add(idDoc);
				//if(Integer.parseInt(idDoc)%100==0) System.out.println("document numero : " + idDoc);
				resultat[nbDocRead] = pool.submit(new Callable<Double>(){
					@Override
					public Double call() throws Exception{
						Double scoreDoc = 0.0;
						Double score;
						RandomAccessFile r = new RandomAccessFile(invertedName, "r");
						for(String stem : query.keySet()){
							if((score = lambda*pmd(stem,idDoc,r) + (1-lambda)*pmc(stem, nbMotsCorpus,r))==0) continue;
							scoreDoc+=(double)query.get(stem)*Math.log(score);
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

	@Override
	public String toString(){
		return "Modele de langue";
	}
	
}