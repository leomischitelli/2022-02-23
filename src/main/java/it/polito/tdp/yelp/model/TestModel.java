package it.polito.tdp.yelp.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model();
		m.creaGrafo("Zpizza", "Scottsdale");
		System.out.println("Numero vertici: " + m.getNumeroVertici());
		System.out.println("\nNumero archi: " + m.getNumeroArchi());
		List<Review> reviews = m.getMaxEdgeReview();
		for(Review r : reviews) {
			System.out.println("\nId vertice massime connessioni: " + r.getReviewId());
			System.out.println("\nNumero archi uscenti: " + m.getNeighborSize(r));
		}
		List<Review> sequenza = m.trovaMiglioramento();
		System.out.println("\nTrovata sequenza massima di dimensione " + sequenza.size());
		for(Review r : sequenza) {
			System.out.println("\n" + r.getReviewId());
		}
		System.out.println("\nGiorni totali: " + m.getGiorniSequenzaMigliore());
	}

}
