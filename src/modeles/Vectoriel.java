package modeles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Vectoriel extends IRmodel{

	private Weighter weighter;
	private boolean normalized;
	
	public Vectoriel(RandomAccessFile index, Weighter weighter, boolean normalized) {
		super(index);
		this.weighter = weighter;
		this.normalized = normalized;
	}

	@Override
	public HashMap<String, Double> getScores(HashMap<String, Integer> query) throws IOException, InterruptedException, ExecutionException {
		// Initialisation des thread et du pool
		int nbThreads = Runtime.getRuntime().availableProcessors()+1;
		ExecutorService pool = Executors.newFixedThreadPool(nbThreads);
		Future<Double> resultat[] = new Future[weighter.getIndexObjet().getDocFrom().getId().size()];
		
		HashMap<String, Double> res = new HashMap<String, Double>();
		HashMap<String,Double> wtq = this.weighter.getWeightsForQuery(query);
		String ligne = "";
		this.index.seek(0);
		
		ArrayList<String> idsDoc = new ArrayList<String>();
		int nbDocRead = 0;

		try {
			while((ligne = this.index.readLine()) != null){
				final String idDoc = ligne.split(":")[0];
				idsDoc.add(idDoc);
				resultat[nbDocRead] = pool.submit(new Callable<Double>(){
					@Override
					public Double call() throws Exception{
						Double scoreDoc = (double) 0;
						RandomAccessFile r = new RandomAccessFile(weighter.getIndexName(), "r");
						HashMap<String,Double> wtd = weighter.getDocWeightsForDoc(idDoc, r);
						for(String stem : wtq.keySet()){
							if(wtd.containsKey(stem)){
								scoreDoc += wtd.get(stem) * wtq.get(stem);
							}
						}
						r.close();
						return scoreDoc;
					}
				});
				nbDocRead++;
			}
			if(this.normalized){
				Double norme = (double) 0;
				for(int i=0; i<weighter.getIndexObjet().getDocFrom().getId().size(); i++)
					norme += Math.pow(resultat[i].get(),2);
				norme=Math.sqrt(norme);
				if(norme!=0)
					for(int i=0; i<weighter.getIndexObjet().getDocFrom().getId().size(); i++)
						res.put(idsDoc.get(i), resultat[i].get()*1.0/norme);
				else
					for(int i=0; i<weighter.getIndexObjet().getDocFrom().getId().size(); i++)
						res.put(idsDoc.get(i), resultat[i].get());
			}else{
				for(int i=0; i<weighter.getIndexObjet().getDocFrom().getId().size(); i++)
					res.put(idsDoc.get(i), resultat[i].get());
			}
			pool.shutdown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public Weighter getWeighter() {
		return weighter;
	}

	public boolean isNormalized() {
		return normalized;
	}
	
	@Override
	public String toString(){
		return "Modele vectoriel avec " + this.weighter.toString();
	}
}
