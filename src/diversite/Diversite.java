package diversite;

import java.io.IOException;
import java.util.TreeMap;

import evaluation.Query;

public abstract class Diversite {
	protected Similarite similarite;
	
	public Diversite(Similarite similarite){
		super();
		this.similarite = similarite;
	}
	
	/**Fonction  qui va modifier le ranking pour diversifier le resultat
	 * @param TreeMap<String,Double> ranking
	 * @return TreeMap<String,Double>
	 * @throws IOException
	 */
	public abstract TreeMap<String,Double> diversify(TreeMap<String,Double> ranking) throws IOException;
	
	/**La même fonction mais qui diversifie les n premier documents
	 * @param TreeMap<String,Double> ranking
	 * @param int n
	 * @return TreeMap<String,Double>
	 * @throws IOException
	 */
	public abstract TreeMap<String,Double> diversify(TreeMap<String,Double> ranking, int n) throws IOException;

}
