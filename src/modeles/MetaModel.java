package modeles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import evaluation.Query;
import features.FeaturersList;

public abstract class MetaModel extends IRmodelFeaturer{

	protected FeaturersList featurersList;
	
	public MetaModel(RandomAccessFile index, FeaturersList featurersList) {
		super(index);
		this.featurersList = featurersList;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected abstract HashMap<String, Double> getScores(Query query) throws IOException;

}
