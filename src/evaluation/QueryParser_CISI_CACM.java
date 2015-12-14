package evaluation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QueryParser_CISI_CACM extends QueryParser{
	public QueryParser_CISI_CACM() {
		super(".I");
	}

	/**
	 * Fonction qui renvoie un tableau de String contentant 
	 * l'id de la requ�te et l'id du document dans une ligne
	 * du fichier .rel
	 * @param ligne
	 * @return
	 */
	public String[] recupererIdIdDoc(String ligne){
		String[] res = new String[2];
		String id="";
		String idDoc="";
		ligne = ligne.trim();
		char c;
		for(int i=0; i<ligne.length(); i++){
			c = ligne.charAt(i);
			if(c!=' ')
				id+=c;
			else
				break;
		}
		
		ligne = ligne.replace(" ", "");
		for(int i=id.length(); i<ligne.length(); i++){
			c = ligne.charAt(i);
			String cs = Character.toString(c);
			if(cs.matches("\\d"))
				idDoc+=c;
			else
				break;
			if(idDoc.length()==4)
				break;
		}
		id = Integer.toString(Integer.parseInt(id));
		idDoc = Integer.toString(Integer.parseInt(idDoc));
		//System.out.println(id);
		//System.out.println(idDoc);
		res[0]=id;
		res[1]=idDoc;
		return res;
	}

	@Override
	public Query getQuery(String str) {
		String st[]=str.split("\n");
		String id="";
		String text="";
		boolean isText = false;
		boolean first = true;
		Map<String, Double> relevants = new HashMap<String, Double>();
		for(String s:st){
			if(s.startsWith(".I")){
				isText=false;
				id=s.substring(3);
				continue;
			}
			if(s.startsWith(".W")){
				isText=true;
				text=s.substring(2);
				continue;
			}
			if(s.startsWith(".N") || s.startsWith(".A"))
				break;
			if(isText){
				if(!first) text+="\n";
				first=false;
				text+=s;
			}
		}
		String ligne = "";
		try {
			while((ligne=rel.readLine())!=null){
				String[] idIdDoc = recupererIdIdDoc(ligne);
				if(idIdDoc[0].equals(id)){
					do{
						if(!idIdDoc[0].equals(id)) break;
						relevants.put(idIdDoc[1], (double)1);
						if((ligne=rel.readLine())==null) break;
						idIdDoc = recupererIdIdDoc(ligne);
					}while(true);
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Query query=new Query(id, text, relevants);
		return query;
	}


}


