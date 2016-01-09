package diversite;

public abstract class DiversiteCluster implements IDiversite{
	protected Clustering clustering;
	
	public DiversiteCluster(Clustering clustering){
		super();
		this.clustering = clustering;
	}
}
