package evaluation;

import java.util.List;

public abstract class EvalMeasure {
	
	/**
	 * Calcul de la précision 
	 * @param nbDocDecouverts
	 * @param i
	 * @return
	 */
	protected double precision(int nbDocDecouverts, int i){
		return (1.0*nbDocDecouverts)/i;
	}
	
	/**
	 * Calcul du rappel
	 * @param nbDocRel
	 * @param nbDocRelIPremiers
	 * @return
	 */
	protected double rappel(int nbDocRel, int nbDocRelIPremiers){
		return (1.0*nbDocRelIPremiers)/nbDocRel;
	}
	
	/**
	 * Retournant le resultat de l'évaluation de la liste ordonnée de l'objet passé en paramètre
	 * @param I
	 * @return
	 */
	protected abstract List<Double> eval(IRList I);
}
