package features;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import evaluation.Query;

public abstract class Featurer implements Serializable{
	
	protected static final long serialVersionUID = 1L;
	
	protected transient RandomAccessFile index;
	protected Map<KeyDQ, List<Double>> features; // liste avec une liste à deux éléments [idDoc, idQuery] et une liste de scores
	protected String nomFichier;
	@SuppressWarnings("unchecked")
	public Featurer(RandomAccessFile index, String nomFichier) throws ClassNotFoundException, IOException {
		super();
		this.index = index;
		this.features = new HashMap<KeyDQ, List<Double>>();
		this.nomFichier = nomFichier;
	}
	
	/**
	 * Revoie la liste des scores du featurer selon une requete et un document
	 * @param idDoc
	 * @param query
	 * @return
	 * @throws IOException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public abstract List<Double> getFeatures(String idDoc, Query query) throws IOException, InterruptedException, ExecutionException;
	
	/**
	 * Modifie l'attribut features selon le featurers qui hérite de la classe
	 * @param query
	 * @throws IOException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public abstract void setListeFeatures(Query query) throws IOException, InterruptedException, ExecutionException;

	/**
	 * Enregistre un objet Featurer
	 * @throws IOException
	 */
	public void enregisterFeaturer() throws IOException{
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
	 * Charge un objet Featurer
	 * @param nomFichier
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Featurer chargerFeaturer(String nomFichier) throws ClassNotFoundException, IOException{
		File fichier =  new File(nomFichier) ;
		ObjectInputStream ois = null;
		Featurer featurer = null;
		try {
			// ouverture d'un flux sur un fichier
			ois = new ObjectInputStream(new FileInputStream(fichier));
			// dï¿½sï¿½rialization de l'objet
			featurer = (Featurer)ois.readObject() ;
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
		return featurer;
	}

	public Map<KeyDQ, List<Double>> getFeatures() {
		return features;
	}

	
}
