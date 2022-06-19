package it.polito.tdp.yelp.model;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private Graph<Review, DefaultWeightedEdge> grafo;
	private YelpDao dao;
	private List<Review> sequenzaMigliore;
	private int totGiorni;
	
	public Model() {
	
		this.dao = new YelpDao();
	}
	
	public List<String> getAllCities(){
		return this.dao.getAllCities();
	}
	
	public List<Business> getAllBusinessCity(String city){
		return this.dao.getAllBusinessCity(city);
	}
	
	public void creaGrafo(String businessId, String city) {
		
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		Set<Review> setVertici = new HashSet<>(this.dao.getBusinessReviews(businessId, city));
		Graphs.addAllVertices(this.grafo, setVertici);
		//itero sui vertici, il set cambiera, creo archi
		for(Review r1 : setVertici) {
				
					for (Review r2 : setVertici) {
						int peso = (int) ChronoUnit.DAYS.between(r1.getDate(), r2.getDate());
						if(peso>0)
							Graphs.addEdge(this.grafo, r1, r2, peso);
				
			}
		}
		
	}
	
	public List<Review> getMaxEdgeReview() {
		
		int max = 0;
		List<Review> maxReview = new ArrayList<>();
		for(Review r : this.grafo.vertexSet()) {
			int numero = this.grafo.outgoingEdgesOf(r).size();
			if(numero>max) {
				max = numero;
				maxReview.clear();
				maxReview.add(r);
			}
			else if(numero==max) {
				maxReview.add(r);
			}
		}
		
		return maxReview;
	}
	
	public int getNeighborSize(Review r) {
		return this.grafo.outgoingEdgesOf(r).size();
	}
	
	public int getNumeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Review> trovaMiglioramento(){
		this.sequenzaMigliore = new ArrayList<Review>();
		this.totGiorni = 0;
		
		for(Review first : this.grafo.vertexSet()) {
			ArrayList<Review> sequenzaAttuale = new ArrayList<Review>();
			sequenzaAttuale.add(first);
			cercaMiglioramento(sequenzaAttuale, this.sequenzaMigliore, 0);
			sequenzaAttuale.remove(0);
		}
		return this.sequenzaMigliore;
	}

	private void cercaMiglioramento(ArrayList<Review> sequenzaAttuale, List<Review> sequenzaMigliore, int giorniAttuali) {
		//genero solo soluzioni valide
		
		if(sequenzaAttuale.size() > sequenzaMigliore.size()) {
			this.sequenzaMigliore = new ArrayList<Review>(sequenzaAttuale);
			this.totGiorni = giorniAttuali;
		}
		
		Review source = sequenzaAttuale.get(sequenzaAttuale.size() - 1); //source = ultimo vertice inserito
		Set<DefaultWeightedEdge> archiUscenti = this.grafo.outgoingEdgesOf(source);
		
		for (DefaultWeightedEdge e : archiUscenti) {
			Review target = this.grafo.getEdgeTarget(e);
			if(target.getStars() >= source.getStars()) {
				//esploro
				sequenzaAttuale.add(target);
				giorniAttuali+=this.grafo.getEdgeWeight(e);
				cercaMiglioramento(sequenzaAttuale, sequenzaMigliore, giorniAttuali);
				giorniAttuali-=this.grafo.getEdgeWeight(e);
				sequenzaAttuale.remove(sequenzaAttuale.size() - 1);
			}
				
		}
		
		
	}
	
	public int getGiorniSequenzaMigliore() {
		return this.totGiorni;
	}
	
}
