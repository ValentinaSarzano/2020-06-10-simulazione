package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.imdb.model.Event.EventType;

public class Simulatore {
	
	//Dati in ingresso
	private int numGiorni; 
	
	//Dati in uscita 
	private int nPause;
	private Map<Integer, Actor> intervistati; //mappa giorno + attore intervistato
	
	
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private List<Actor> ancoraDisponibili;
	
	//Coda degli eventi
	private PriorityQueue<Event> queue;
	
	
	//Nel costruttore i parametri che restano costati per tutta la durata del processo
	public Simulatore(int numGiorni, Graph<Actor, DefaultWeightedEdge> grafo) {
		super();
		this.numGiorni = numGiorni;
		this.grafo = grafo;
		
	}

	//Getters+setters --> dati in input, Getters --> dati in output
	
	public int getNumGiorni() {
		return numGiorni;
	}


	public void setNumGiorni(int numGiorni) {
		this.numGiorni = numGiorni;
	}


	public int getnPause() {
		return nPause;
	}


	public Map<Integer, Actor> getIntervistati() {
		return intervistati;
	}
	
	
	public void init(int n) {
		
		this.ancoraDisponibili = new ArrayList<Actor>(this.grafo.vertexSet());
		this.intervistati = new HashMap<Integer, Actor>();
		this.queue = new PriorityQueue<Event>();
		
		this.nPause = 0;
		
		//pre-carico la coda con le interviste del primo giorno:
		//ATTENZIONE --> una volta che ho selezionato un 
		//attore non dovra piu essere possibile estrarlo
		//nell'estrazione successiva (lo metto nella "blacklist"
		//di attori da non prendere piu, ovvero nella lista di intervistati)
	
	
		Actor intervistato = selezionaIntervistato(ancoraDisponibili);
		this.intervistati.put(0, intervistato);
		this.ancoraDisponibili.remove(intervistato);
		Event e = new Event (0, EventType.DA_INTERVISTARE, intervistato);
		this.queue.add(e);
		System.out.println(queue);
	
	
	}


	public void run() {
		while(!this.queue.isEmpty() && this.intervistati.size() < numGiorni) { //Finchè la coda non è vuota e finchè non ho intervistato almeno un attore al giorno
			Event e = this.queue.poll(); //Estraggo l'evento
			processEvent(e);
		}
		
	}
	

	private void processEvent(Event e) {
		
		
		for(int giorno = 1; giorno < this.numGiorni; giorno++) {
			
			int indice = this.ancoraDisponibili.size();
			int indiceRandom = (int) Math.random()*(indice+1);
			
			//Giorno successivo al giorno di pausa, sceglie casualmente chi intervistare
			if(!this.intervistati.containsKey(giorno-1)) {
				   Actor intervistato = this.ancoraDisponibili.get(indiceRandom);
				   this.queue.add(new Event(giorno, EventType.DA_INTERVISTARE, intervistato));
				   this.intervistati.put(giorno, intervistato);
			}

			// Attori delllo stesso genere per due giorni di fila
			if(giorno >=2 && this.intervistati.containsKey(giorno-2) && this.intervistati.containsKey(giorno-2) && this.intervistati.get(giorno-1).getGender().equals(this.intervistati.get(giorno-2).getGender())) {
				if(Math.random()>=0.1) {
					this.nPause++;
			    }
			}
			
				
		    //Caso I: Con il 60% di probabilita scelgo a caso
				
				if(Math.random() <= 0.6) {
					Actor intervistato = this.ancoraDisponibili.get(indiceRandom);
					this.intervistati.put(giorno, intervistato);
					this.ancoraDisponibili.remove(intervistato);
					this.queue.add(new Event(giorno, EventType.DA_INTERVISTARE, intervistato));
				
				} else if(Math.random() <= 0.4) {
					   
					   List<Actor> listaIntervistati = new ArrayList<>(intervistati.values());
					   Actor ultimoIntervistato = listaIntervistati.get(listaIntervistati.size()-1);
					   Actor intervistato = suggerisciIntervistato(ultimoIntervistato);
					   this.queue.add(new Event(e.getGiorno()+1, EventType.DA_INTERVISTARE, intervistato));
					   this.intervistati.put(giorno, intervistato);
					
				}
		}
		System.out.println(queue);
	}


	private Actor suggerisciIntervistato(Actor ultimoIntervistato) {
		int pesoMax = 0;
		Actor scelto = null;
		for(Actor a: Graphs.neighborListOf(this.grafo, ultimoIntervistato)) {
			int peso = (int) this.grafo.getEdgeWeight(this.grafo.getEdge(a, ultimoIntervistato));
		    if(peso > pesoMax) {
		    	pesoMax = peso;
		    }
		}
		
		//A questo punto abbiamo il pesoMax
		List<Actor> vannoBene = new ArrayList<>();
		for(Actor a: Graphs.neighborListOf(this.grafo, ultimoIntervistato)) {
			int peso = (int) this.grafo.getEdgeWeight(this.grafo.getEdge(a, ultimoIntervistato));
		    if(peso == pesoMax) {
		    	vannoBene.add(a);
		    }
		}
		scelto = selezionaIntervistato(vannoBene);
		
		return scelto;
	}


	private Actor selezionaIntervistato(List<Actor> lista) {
		Set<Actor> candidati = new HashSet<Actor>(lista); //Metto dentro al set la lista
		candidati.removeAll(new HashSet<Actor>(this.intervistati.values()));
		
		int scelto = (int)(Math.random()*candidati.size());
		Actor selezionato = new ArrayList<Actor>(candidati).get(scelto);
		return selezionato;
	}


	
}
