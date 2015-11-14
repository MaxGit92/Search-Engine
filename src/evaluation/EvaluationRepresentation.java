package evaluation;

import java.util.List;

// Classe qui permet simplement d'avoir un nom de modï¿½le et de mesure associï¿½s ï¿½ un rï¿½sultat de mesure d'ï¿½valuation 
// selon des requetes et selon un modï¿½le de recherche
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
		return nomModele+", mesure "+nomMesure+", requête numéro "+query.getId()+", \nScore:\n"+score.toString();
	}
}
