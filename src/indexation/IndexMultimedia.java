package indexation;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;


public class IndexMultimedia extends Index{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IndexMultimedia(String name, Parser parser,
			TextRepresenter textRepresenter) {
		super(name, parser, textRepresenter);
	}

	@Override
	public void indexation(){
		try {
			/* Fichiers index en lecture �criture. */
			this.index = new RandomAccessFile(this.name+"_index", "rw");
			this.inverted = new RandomAccessFile(this.name+"_inverted", "rw");
			/*
			 * Si les fichier ne sont pas vide on les vide
			 */
			if(this.index.length() > 0)
				this.index.setLength(0);
			if(this.inverted.length() > 0)
				this.inverted.setLength(0);
			/* On initialise un parser sur name qui est le nom du fichier qui contient
			 * le corpus.
			 */
			this.parser.init(this.name+".txt");

			/* auxDoc est le nouveau document que sur lequel on va trvailler
			 * � chaque it�ration. 
			 */
			Document auxDoc;
			/* On d�finit une hashmap stems_doc pour enregistrer les stems sur chaque document.
			 * 
			 */
			HashMap<String, ArrayList<Integer[]>> stems_doc = new HashMap<String, ArrayList<Integer[]>>();
			while((auxDoc=this.parser.nextDocument()) != null){
				/* On remplit l'objet docFrom avec les donn�es de aucDoc */
				this.docFrom.getId().add(auxDoc.getId());
				String[] infos = auxDoc.get("from").split(";");
				this.docFrom.getFileSource().put(auxDoc.getId(), infos[0]);
				this.docFrom.getPos().put(auxDoc.getId(), Long.parseLong(infos[1]));
				this.docFrom.getLongueur().put(auxDoc.getId(),Long.parseLong(infos[2]));
				this.docFrom.getPosInIndex().put(auxDoc.getId(), this.index.length());

				/* Avec le stemmer on cr�� une hashMap avec les stems et
				 * leur nombre d'apparitions dans le document.
				 */
				HashMap<String, Integer> hashStems = this.textRepresenter.getTextRepresentation(auxDoc.getText());
				this.docFrom.getNbMots().put(auxDoc.getId(), (long) hashStems.size());
				try {
					/* On �crit dans l'index */
					this.index.writeBytes(auxDoc.getId()+":");
					for(String key : hashStems.keySet()){
						this.index.writeBytes(key + "-" + hashStems.get(key)+";");
						/* On ins�re les �l�ments dans stems_doc pour ensuite cr�er inverted */
						if(!stems_doc.containsKey(key))
							stems_doc.put(key, new ArrayList<Integer[]>());
						Integer[] tab = new Integer[2];
						tab[0] = Integer.parseInt(auxDoc.getId());
						tab[1] = hashStems.get(key);
						stems_doc.get(key).add(tab);
					}
					this.index.writeBytes("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			/*
			 * On met les valeurs de stems_doc dans inverted
			 */
			for(String key : stems_doc.keySet()){
				try {
					this.stems.put(key,this.inverted.length());
					this.inverted.writeBytes(key+":");
					for(Integer[] tab : stems_doc.get(key)){
						this.inverted.writeBytes(tab[0].toString()+"-"+tab[1].toString()+ ";");
					}
					this.inverted.writeBytes("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			/* On ferme nos RandomAccessFile */
			try {
				this.index.close();
				this.inverted.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
}

