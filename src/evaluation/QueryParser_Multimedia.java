package evaluation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QueryParser_Multimedia extends QueryParser{

	public QueryParser_Multimedia() {
		super(".I");
	}

	@Override
	public Query getQuery(String str) {
		String st[]=str.split("\n");
		String id="";
		String text="";
		boolean isText = false;
		boolean first = true;
		Map<String, Double> relevants = new HashMap<String, Double>();
		Map<String, String> clusters = new HashMap<String, String>();
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
				// tableau de taille 4 avec :
				// idQuerry idDoc impportance cluster
				String[] idIdDocCluster = ligne.split(" ");
				if(idIdDocCluster[0].equals(id)){
					do{
						if(!idIdDocCluster[0].equals(id)) break;
						relevants.put(idIdDocCluster[1], (double)1);
						clusters.put(idIdDocCluster[1], idIdDocCluster[3]);
						if((ligne=rel.readLine())==null) break;
						idIdDocCluster = ligne.split(" ");
					}while(true);
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Query query=new Query(id, text, relevants, clusters);
		return query;
	}


}
