package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	private ImdbDAO dao;
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private Map<Integer, Actor> idMap;
	
	private Simulatore sim;
	
	public Model() {
		super();
		this.dao = new ImdbDAO();
	}
	
	public List<String> getAllGenres(){
		return this.dao.getAllGenres();
	}
	
	public void creaGrafo(String genere) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		
		this.dao.getVertici(genere, idMap);
		
		//Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungo gli archi
		for(Adiacenza a: this.dao.getAdiacenze(genere, idMap)) {
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
	
	public List<Actor> getVertici(){
		List<Actor> vertici = new ArrayList<>(this.grafo.vertexSet());
		 Collections.sort(vertici, new Comparator<Actor>() {

				@Override
				public int compare(Actor o1, Actor o2) {
					return o1.getLastName().compareTo(o2.getLastName());
				}
		    	
		    });
		    return vertici;
	}
	
	public List<Actor> getAttoriSimili(Actor a){
		
		Set<Actor> componenteConnessa = new HashSet<>();
	    ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<>(this.grafo);
	    componenteConnessa = ci.connectedSetOf(a);
	    componenteConnessa.remove(a);
	    
	    List<Actor> simili = new ArrayList<>(componenteConnessa);
	    
	    Collections.sort(simili, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				return o1.getLastName().compareTo(o2.getLastName());
			}
	    	
	    });
	    return simili;
	}
	public void simula(int n) {
		sim = new Simulatore(n, this.grafo);
		sim.init(n);
		sim.run();
		
	}
	
	public List<Actor> getIntervistati(){
		if(sim == null) {
			return null;
		}
		return new ArrayList<>(sim.getIntervistati().values());
		
	}
	
	public Integer getPause() {
		if(sim == null) {
			return null;
		}
		return sim.getnPause();
	}

}
