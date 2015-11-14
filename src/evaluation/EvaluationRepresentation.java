package evaluation;

import java.util.List;

// Classe qui permet simplement d'avoir un nom de mod�le et de mesure associ�s � un r�sultat de mesure d'�valuation 
// selon des requetes et selon un mod�le de recherche
public class EvaluationRepresentation {
	private String nomModele;
	private String nomMesure;
	private Query query;
	private List<Double> score;
	
	public EvaluationRepresentation(String nomModele, String nomMesure, Query query,
	List<Double> score) {
		super();
		this.nomModele = nomModele;
		this.nomMesure = nomMesure;
		this.score = score;
		this.query = query;
	}

	public String getNomModele() {
		return nomModele;
	}

	public String getNomMesure() {
		return nomMesure;
	}

	public List<Double> getScore() {
		return score;
	}
	
	public Query getQuery() {
		return query;
	}
	
	@Override
	public String toString(){
		return nomModele+", mesure "+nomMesure+", requ�te num�ro "+query.getId()+", \nScore:\n"+score.toString();
	}
}
