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
import modeles.Weighter;

public class SimilariteVectoriel extends Similarite{

	private Weighter weighter;
	
	public SimilariteVectoriel(Index indexObjet, RandomAccessFile index, RandomAccessFile inverted, Weighter weighter) {
		super(indexObjet, index, inverted);
		this.weighter = weighter;
	}

	@Override
	public double similarite(String idDoc1, String idDoc2) throws IOException {
		Double scoreDoc = (double) 0;
		HashMap<String,Double> wtd1 = weighter.getDocWeightsForDoc(idDoc1, index);
		HashMap<String,Double> wtd2 = weighter.getDocWeightsForDoc(idDoc2, index);

		for(String stem : wtd1.keySet()){
			if(wtd2.containsKey(stem)){
				scoreDoc += wtd1.get(stem) * wtd2.get(stem);
			}
		}
		return scoreDoc;// On normalise par rapport à l'ensemble du corpus
	}
	
	
	@Override
	public double[][] allScores() {
		// TODO Auto-generated method stub
		return null;
	}

}
