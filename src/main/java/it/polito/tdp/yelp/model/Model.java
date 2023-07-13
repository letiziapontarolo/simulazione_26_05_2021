package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private Graph<Business, DefaultWeightedEdge> grafo;
	private YelpDao dao;
	private Map<String, Business> businessIdMap;
	private List<Arco> archi;
	
	public Model() {
	   dao = new YelpDao();
	}
	
	public boolean verifica() {
		if (businessIdMap.values().isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public List<String> listaCitta() {
		return this.dao.listaCity();
	}
	
	public List<Integer> listaAnni() {
		return this.dao.listaAnni();
	}
	
	public void creaGrafo(String city, int anno) {
		
		grafo = new SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		businessIdMap = new HashMap<String, Business>();
		archi = new ArrayList<Arco>();
		
		this.dao.creaVertici(businessIdMap, city, anno);
		Graphs.addAllVertices(this.grafo, businessIdMap.values());
		
		for (Business b1 : this.grafo.vertexSet()) {
			for (Business b2 : this.grafo.vertexSet()) {
				if (b1.getBusinessId().compareTo(b2.getBusinessId()) > 0) {
					double differenza = b1.getMedia() - b2.getMedia();
					if (differenza > 0) {
						Graphs.addEdgeWithVertices(this.grafo, b2, b1, differenza);
					}
					if (differenza < 0) {
						Graphs.addEdgeWithVertices(this.grafo, b1, b2, -differenza);
					}
				}
			}
		}
		
	}
	
	public String localeMigliore() {
		
		String result = "";
		double max = 0;
		
		for (Business b : this.grafo.vertexSet()) {
			double pesoEntranti = 0;
			double pesoUscenti = 0;
			Set<DefaultWeightedEdge> archiEntranti = this.grafo.incomingEdgesOf(b);
			for (DefaultWeightedEdge e : archiEntranti) {
				pesoEntranti = pesoEntranti + this.grafo.getEdgeWeight(e);
			}
			Set<DefaultWeightedEdge> archiUscenti = this.grafo.outgoingEdgesOf(b);
			for (DefaultWeightedEdge e : archiUscenti) {
				pesoUscenti = pesoUscenti + this.grafo.getEdgeWeight(e);
			}
			
			double differenza = pesoEntranti - pesoUscenti;
			b.setDifferenza(differenza);
			if (differenza > max) {
				max = differenza;
			}

		}
		
		for (Business bb : this.grafo.vertexSet()) {
			if (bb.getDifferenza() == max) {
				result = result + bb.getBusinessName();
			}
		}
		return result;
	}
	
	public int numeroVertici() {
		return this.grafo.vertexSet().size();
		}
	
		 public int numeroArchi() {
		return this.grafo.edgeSet().size();
		}
	

	
	
}
