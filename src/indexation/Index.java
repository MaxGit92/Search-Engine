package indexation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Index implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	// Attributs
	protected TextRepresenter textRepresenter;
	protected Parser parser;
	protected String name;
	
	// Variables
	protected transient RandomAccessFile index;
	protected transient RandomAccessFile inverted;
	protected Map<Integer, Integer> docs; // cf docFrom
	protected Map<String, Long> stems = new HashMap<String, Long>(); // contient la position des stems pour faciliter l'acc�s � l'index invers�
	protected DocFrom docFrom = new DocFrom();

	// Constructeur
	public Index(String name, Parser parser, TextRepresenter textRepresenter){
		this.name = name;
		this.parser = parser;
		this.textRepresenter = textRepresenter;
	}
	
	/**
	 * Fonction qui cr�� deux fichiers l'index et l'index invers�
	 * pour le corpus nomm� name (qui est une de ses variable d'instance)
	 */
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
				String[] linksSplit = auxDoc.getOther().get("links").split(";");
				HashSet<String> links = new HashSet<String>();
				for(String l : linksSplit)
					links.add(l);
				this.docFrom.getLinksDoc().put(auxDoc.getId(), links);
				
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
	
	
	/**
	 * Retourne une map avec en cl� les stems et en valeur le nombre d'apparition dans le document dont l'id est id.
	 * @param id
	 * @param index
	 * @return HashMap<String,Double>
	 */
	public HashMap<String,Double> getTfsForDoc(String id, RandomAccessFile index){
		HashMap<String,Double> res = new HashMap<String,Double>();
		try {
			index.seek(this.docFrom.getPosInIndex().get(id));
			String s = index.readLine();
			s = s.split(":")[1];
			String[] stems_tfs = s.split(";");
			for(String stem_tf : stems_tfs){
				String stem= stem_tf.split("-")[0];
				Double tf = Double.parseDouble(stem_tf.split("-")[1]);
				res.put(stem, tf);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * Retourne une map avec en cl�e l'id des documents et en valeur le nombre d'apparition du stem dans chaque document.
	 * @param stem
	 * @param nomInverted
	 * @return HashMap<String,Double>
	 */
	public HashMap<String,Double> getTfsForStem(String stem, RandomAccessFile inverted){
		HashMap<String,Double> res = new HashMap<String,Double>();
		try {
			if(this.stems.get(stem) == null){
				return res;
			}
			inverted.seek(this.stems.get(stem));
			String s = inverted.readLine();
			s = s.split(":")[1];
			String[] docs_tfs = s.split(";");
			for(String doc_tf : docs_tfs){
				String doc= doc_tf.split("-")[0];
				Double tf = Double.parseDouble(doc_tf.split("-")[1]);
				res.put(doc, tf);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Retourne le nombre d'apparition du stem dans un document.
	 * @param stem
	 * @param nomInverted
	 * @return HashMap<String,Double>
	 */
	public Integer getTfForStem(String stem, String idDoc, RandomAccessFile inverted) throws IOException{
		Long pos_inverted = stems.get(stem);
		if(pos_inverted==null) return 0; // Si le stem n'est pas dans l'index inversé
		inverted.seek(pos_inverted);
		String ligne = inverted.readLine();
		ligne = ligne.split(":")[1];
		String[] split = ligne.split(";");
		for(String s : split){
			String[] ss = s.split("-");
			if(ss[0].equals(idDoc)){
				return Integer.parseInt(ss[1]);
			}
		}
		return 0;
	}
	
	public String getStrDoc(String id){
		String idReel="";
		for(String i : this.docFrom.getId()){
			if(i.equals(id)){
				idReel=i;
				break;
			}
		}
		long pos = this.docFrom.getPos().get(idReel);
		long taille = this.docFrom.getLongueur().get(idReel);
		this.parser.init(this.name+".txt");
		String res = "";
		try {
			this.parser.br.seek(pos);
			while(res.length() < taille)
				res += this.parser.br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * serialise l'objet index dans le fichier nomm� nomFichier
	 * @param nomFichier
	 * @throws IOException
	 */
	public void enregisterObjetIndex(String nomFichier) throws IOException{
		File fichier =  new File(nomFichier) ;
		ObjectOutputStream oos = null;
		try {
			 // ouverture d'un flux sur un fichier
			oos = new ObjectOutputStream(new FileOutputStream(fichier));
			 // serialization de l'objet
			oos.writeObject(this) ;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			// fermeture du flux
			oos.close();
		}
		
	}
	
	/**
	 * M�thode static qui renvoie un Index qui est s�rialis� dans le fichier nomm� nomfichier. 
	 * @param nomFichier
	 * @return Index
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Index chargerObjetIndex(String nomFichier) throws IOException, ClassNotFoundException{
		File fichier =  new File(nomFichier) ;
		ObjectInputStream ois = null;
		Index index = null;
		try {
			// ouverture d'un flux sur un fichier
			ois = new ObjectInputStream(new FileInputStream(fichier));
			// d�s�rialization de l'objet
			index = (Index)ois.readObject() ;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Fermeture du fihcier
			ois.close();
		}
		return index;
	}
	
	public HashMap<String,Integer> getRepresentation(String texte){
		return textRepresenter.getTextRepresentation(texte);
	}
	
	
	public DocFrom getDocFrom() {
		return docFrom;
	}

	public Map<String, Long> getStems() {
		return stems;
	}


}

