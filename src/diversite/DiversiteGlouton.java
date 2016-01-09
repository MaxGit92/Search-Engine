package diversite;

public abstract class DiversiteGlouton implements IDiversite{
	protected Similarite similarite;
	
	public DiversiteGlouton(Similarite similarite){
		super();
		this.similarite = similarite;
	}
}
