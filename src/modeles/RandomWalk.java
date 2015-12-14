package modeles;

import java.util.HashSet;
import java.util.Map;


public abstract class RandomWalk {
	/**
	 * Une marche aléatoire à redéfinir selon la classe qui hérite de RandomWalk
	 * @param graphe
	 * @return
	 */
	public abstract Map<String, Double> marcheAleatoire(Map<String, HashSet<String>> graphe);
}
