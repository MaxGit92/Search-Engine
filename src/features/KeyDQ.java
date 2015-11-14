package features;

import java.io.Serializable;

public class KeyDQ implements Serializable{
	/**
	 * Objet qui permet d'avoir un doublon avec d'id du document et de la query
	 * Cela permet d'indexer dans la hashmap les scores des featurers
	 */
	private static final long serialVersionUID = 5610092829953213486L;
	private final String idDoc;
	private final String idQuery;
	
	public KeyDQ(String idDoc, String idQuery) {
		super();
		this.idDoc = idDoc;
		this.idQuery = idQuery;
	}
	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(!(o instanceof KeyDQ)) return false;
		KeyDQ keyDQ = (KeyDQ) o;
		return idDoc.equals(keyDQ.idDoc) && idQuery.equals(keyDQ.idQuery);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idDoc == null) ? 0 : idDoc.hashCode());
		result = prime * result + ((idQuery == null) ? 0 : idQuery.hashCode());
		return result;
	}
	
	public String getIdDoc(){
		return new String(idDoc);
	}
	
	public String getIdQuery(){
		return new String(idQuery);
	}
	
	

}
