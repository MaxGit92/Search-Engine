package diversite;

import java.io.IOException;
import java.util.TreeMap;

public interface IDiversite {

	/**Fonction  qui va modifier le ranking pour diversifier le resultat
	 * @param TreeMap<String,Double> ranking
	 * @return TreeMap<String,Double>
	 * @throws IOException
	 * @throws Exception 
	 */
	public abstract TreeMap<String,Double> diversify(TreeMap<String,Double> ranking) throws IOException, Exception;
	
	/**La même fonction mais qui diversifie les n premier documents
	 * @param TreeMap<String,Double> ranking
	 * @param int n
	 * @return TreeMap<String,Double>
	 * @throws IOException
	 * @throws Exception 
	 */
	public abstract TreeMap<String,Double> diversify(TreeMap<String,Double> ranking, int n) throws IOException, Exception;

}
