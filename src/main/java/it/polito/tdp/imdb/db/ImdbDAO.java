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
	
	public void getVertici(String genere, Map<Integer, Actor> idMap) {
		String sql = "SELECT DISTINCT a.id, a.first_name, a.last_name, a.gender "
				+ "FROM actors a, roles r, movies_genres mg "
				+ "WHERE a.id = r.actor_id "
				+ "AND mg.movie_id = r.movie_id "
				+ "AND mg.genre = ? ";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("a.id"))) {
					Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
							res.getString("gender"));
					idMap.put(res.getInt("a.id"), actor);
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String genere, Map<Integer, Actor> idMap){
		String sql = "SELECT a1.id AS id1, a2.id AS id2, COUNT(mg1.movie_id) AS peso "
				+ "FROM actors a1, actors a2, roles r1, roles r2, movies_genres mg1, movies_genres mg2 "
				+ "WHERE a1.id < a2.id "
				+ "AND a1.id = r1.actor_id AND a2.id = r2.actor_id "
				+ "AND mg1.movie_id = r1.movie_id AND mg2.movie_id = r2.movie_id AND mg1.movie_id = mg2.movie_id "
				+ "AND mg1.genre = mg2.genre AND mg1.genre = ? "
				+ "GROUP BY id1, id2";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getInt("id1")) && idMap.containsKey(res.getInt("id2"))) {
					Adiacenza a = new Adiacenza(idMap.get(res.getInt("id1")), idMap.get(res.getInt("id2")), res.getInt("peso"));
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
	
	
}
