package modeles;

import java.util.HashSet;
import java.util.Map;


public abstract class RandomWalk {
	/**
	 * Une marche al�atoire � red�finir selon la classe qui h�rite de RandomWalk
	 * @param graphe
	 * @return
	 */
	public abstract Map<String, Double> marcheAleatoire(Map<String, HashSet<String>> graphe);
}
