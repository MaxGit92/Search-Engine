package main;

import java.io.IOException;

import indexation.Index;
import indexation.Parser;
import indexation.ParserCISI_CACM;
import indexation.Stemmer;

public class CreationIndex {

	public static void main(String[] args) throws IOException {
		/*
		 * PACKAGE INDEXATION
		 */
		
		// CRATION DES INDEX
		// Definition des parametres
		System.out.println("Indexation");
		String fichierCisi = "cisi";
		String fichierCacm = "cacm";
		Parser p = new ParserCISI_CACM();
		Stemmer s = new Stemmer();
		// création des index
		Index indexCisi = new Index(fichierCisi, p, s);
		Index indexCacm = new Index(fichierCacm, p, s);
		// execution de l'indexation
		indexCisi.indexation();
		indexCacm.indexation();
		// serialisation
		indexCisi.enregisterObjetIndex("cisi.ser");
		indexCacm.enregisterObjetIndex("cacm.ser");
		
	}

}
