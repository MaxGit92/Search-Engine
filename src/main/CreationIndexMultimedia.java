package main;

import java.io.IOException;

import indexation.Index;
import indexation.IndexMultimedia;
import indexation.Parser;
import indexation.ParserCISI_CACM;
import indexation.Stemmer;

public class CreationIndexMultimedia {

	public static void main(String[] args) throws IOException {
		/*
		 * PACKAGE INDEXATION
		 */
		
		// CRATION DES INDEX
		// Definition des parametres
		System.out.println("Indexation");
		String easy235 = "easyCLEF08_text";
		Parser p = new ParserCISI_CACM();
		Stemmer s = new Stemmer();
		// création des index
		Index indexEasy235= new IndexMultimedia(easy235, p, s);
		// execution de l'indexation
		indexEasy235.indexation();
		// serialisation
		indexEasy235.enregisterObjetIndex("easyCLEF08.ser");

	}

}
