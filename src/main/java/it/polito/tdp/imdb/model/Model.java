package it.polito.tdp.imdb.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map<Integer, Actor> idMap;
	
	public Model() {
		super();
		this.dao = new ImdbDAO();
	}
	
	public List<String> getAllGenres(){
		return this.dao.getAllGenres();
	}
	
	public void creaGrafo(String g) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	    this.idMap = new HashMap<>();
	    
	    this.dao.getVertici(g, idMap);
	    
	    //Aggiunta vertici
	    Graphs.addAllVertices(this.grafo, idMap.values());
	    
	    //Aggiunta archi
	    for(Adiacenza a: this.dao.getAdiacenze(g, idMap)) {
	    	if(this.grafo.containsVertex(a.getA1()) && this.grafo.containsVertex(a.getA2())) {
	    		Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
	    	}
	    }
	
	    System.out.println("Grafo creato!");
		System.out.println("#VERTICI: "+ this.grafo.vertexSet().size());
		System.out.println("#ARCHI: "+ this.grafo.edgeSet().size());
		
	
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public boolean grafoCreato() {
		if(this.grafo == null)
			return false;
		else 
			return true;
	}

}
