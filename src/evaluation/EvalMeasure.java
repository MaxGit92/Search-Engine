package evaluation;

import java.util.List;

public abstract class EvalMeasure {
	
	/**
	 * Calcul de la pr�cision 
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
	 * Retournant le resultat de l'�valuation de la liste ordonn�e de l'objet pass� en param�tre
	 * @param I
	 * @return
	 */
	protected abstract List<Double> eval(IRList I);
}
