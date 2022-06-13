package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getVertici(String genre, Map<Integer, Actor> idMap) {
		String sql ="SELECT DISTINCT a.id, a.first_name, a.last_name, a.gender "
				+ "FROM actors a, roles r, movies_genres m "
				+ "WHERE a.id = r.actor_id AND r.movie_id = m.movie_id AND m.genre = ? "
				+ "ORDER BY id ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genre);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("a.id"))) {
				Actor actor = new Actor(res.getInt("a.id"), res.getString("a.first_name"), res.getString("a.last_name"),
						res.getString("a.gender"));
				idMap.put(res.getInt("a.id"), actor);
				}
				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	public List<Adiacenza> getAdiacenze(String genre, Map<Integer, Actor> idMap){
		String sql = "SELECT a1.id AS id1, a2.id AS id2, COUNT(DISTINCT r1.movie_id) AS peso "
				+ "FROM actors a1, actors a2, roles r1, roles r2, movies_genres m1, movies_genres m2 "
				+ "WHERE a1.id < a2.id "
				+ "AND r1.actor_id = a1.id AND r2.actor_id = a2.id "
				+ "AND r1.movie_id = r2.movie_id "
				+ "AND r1.movie_id = m1.movie_id AND r2.movie_id = m2.movie_id "
				+ "AND m1.genre = ? AND m1.genre = m2.genre "
				+ "GROUP BY a1.id, a2.id";
		
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genre);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getInt("id1")) && idMap.containsKey(res.getInt("id2"))) {
					Adiacenza a = new Adiacenza(idMap.get(res.getInt("id1")),idMap.get(res.getInt("id2")), res.getInt("peso"));
				    result.add(a);
				}
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<String> getAllGenres(){
		String sql = "SELECT DISTINCT genre "
				+ "FROM movies_genres "
				+ "ORDER BY genre";
		List<String> genres = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				String genere = res.getString("genre");
				genres.add(genere);
			}
			conn.close();
			return genres;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	
	
}
