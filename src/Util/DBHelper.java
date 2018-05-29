package Util;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.mysql.fabric.xmlrpc.base.Array;
import com.mysql.jdbc.PreparedStatement;

import MusicLibrary.Song;
import Util.DBConfig;

/**
 * A class that provides static method to access a relational database.
 * Relies on the database drivers here: https://dev.mysql.com/downloads/connector/j/
 * @author srollins
 *
 */
public class DBHelper {

	/**
	 * Creates a table called artist in the database specified by the configuration information.
	 * The table must have four columns:
	 * name - should be a 100 character string that cannot be null and is the primary key
	 * listeners - an integer
	 * playcount - an integer
	 * bio - a long text string
	 * 
	 * @param dbconfig
	 * @throws SQLException
	 */
	public static void createArtistTable(DBConfig dbconfig) throws SQLException {		
		Connection con = getConnection(dbconfig);
		Statement stmt = con.createStatement();
		
		if(!tableExists(con, "artist")) {
			stmt.executeUpdate("CREATE TABLE artist(name varchar(100) NOT NULL PRIMARY KEY, listeners int, playcount int, bio longtext);");
		}
		con.close();
	}
	
	/**
	 * Creates users table if it does not already exist
	 * @param dbconfig
	 * @throws SQLException
	 */
	private static void createUsersTable(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.executeUpdate("CREATE TABLE users(name varchar(100), username varchar(100) NOT NULL PRIMARY KEY, password varchar(100));");
		
		con.close();
	}
	
	/**
	 * Creates likes database
	 * @param con
	 * @throws SQLException
	 */
	private static void createFavoritesTable(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.executeUpdate("CREATE TABLE favorites(username varchar(100), trackID varchar(100));");
		
		con.close();
	}

	
	/**
	 * A helper method that returns a database connection.
	 * A calling method is responsible for closing the connection when finished.
	 * @param dbconfig
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(DBConfig dbconfig) throws SQLException {

		String username  = dbconfig.getUsername();
		String password  = dbconfig.getPassword();
		String db  = dbconfig.getDb();

		try {
			// load driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e) {
			System.err.println("Can't find driver");
			System.exit(1);
		}

		// format "jdbc:mysql://[hostname][:port]/[dbname]"
		//note: if connecting through an ssh tunnel make sure to use 127.0.0.1 and
		//also to that the ports are set up correctly
		String host = dbconfig.getHost();
		String port = dbconfig.getPort();
		String urlString = "jdbc:mysql://" + host + ":" + port + "/"+db;
		Connection con = DriverManager.getConnection(urlString,
				username,
				password);

		return con;
	}

	/**
	 * If the artist table exists in the database, removes that table.
	 * 
	 * @param dbconfig
	 * @param tables
	 * @throws SQLException
	 */
	public static void clearTables(DBConfig dbconfig, ArrayList<String> tables) throws SQLException {

		Connection con = getConnection(dbconfig);

		for(String table: tables) {
			//create a statement object
			Statement stmt = con.createStatement();
			if(tableExists(con, table)) {
				String dropStmt = "DROP TABLE " + table;
				stmt.executeUpdate(dropStmt);
			}

		}
		con.close();
	}

	/**
	 * Helper method that determines whether a table exists in the database.
	 * @param con
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	private static boolean tableExists(Connection con, String table) throws SQLException {

		DatabaseMetaData metadata = con.getMetaData();
		ResultSet resultSet;
		resultSet = metadata.getTables(null, null, table, null);

		if(resultSet.next()) {
			// Table exists
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method that determines weather a user exists or not
	 * @param con
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public static boolean userExists(DBConfig dbConfig, String username) throws SQLException {

		Connection con = getConnection(dbConfig);
		
		if(tableExists(con, "users")) {
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM users WHERE username='"+username+"'");
	
			if(resultSet.next()) {
				// User exists
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Takes information about an artist and adds it to the database
	 * @param dbconfig
	 * @param artist
	 * @param listeners
	 * @param playCount
	 * @param bio
	 * @throws SQLException
	 */
	public static void addArtist(DBConfig dbconfig, String artist, int listeners, int playCount, String bio) throws SQLException {

		Connection con = getConnection(dbconfig);
		
		if(tableExists(con, "artist")) {
			PreparedStatement stmt = (PreparedStatement) con.prepareStatement("INSERT INTO artist (name, listeners, playcount, bio) VALUES (?, ?, ?, ?)");
			stmt.setString(1, artist);
			stmt.setInt(2, listeners);
			stmt.setInt(3, playCount);
			stmt.setString(4, bio);
			stmt.executeUpdate();
		}
		
		con.close();
	}
	
	public static boolean validateLogin(DBConfig dbconfig, String username, String password) throws SQLException {
		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
		stmt.setString(1, username);
		stmt.setString(2, password);
		ResultSet resultSet = stmt.executeQuery();
		try {
			return resultSet.next();
		} finally {
			con.close();
		}
	}
	
	public static void addUser(DBConfig dbconfig, String name, String username, String password1) throws SQLException {

		Connection con = getConnection(dbconfig);
		
		if(!tableExists(con, "users")) {
			createUsersTable(con);
		}
		
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("INSERT INTO users (name, username, password) VALUES (?, ?, ?)");
		stmt.setString(1, name);
		stmt.setString(2, username);
		stmt.setString(3, password1);
		stmt.executeUpdate();
			
		con.close();
	}
	
	private static boolean favoriteExists(Connection con, String username, String trackId) throws SQLException {
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("SELECT * FROM favorites WHERE username = ? AND trackID = ?");
		stmt.setString(1, username);
		stmt.setString(2, trackId);
		ResultSet resultSet = stmt.executeQuery();
		return resultSet.next();
	}
	
	public static void addFavorite(DBConfig dbconfig, String username, String trackID) throws SQLException {

		Connection con = getConnection(dbconfig);
		
		if(!tableExists(con, "favorites")) {
			createFavoritesTable(con);
		}
		
		if(!favoriteExists(con, username, trackID)) {
			PreparedStatement stmt = (PreparedStatement) con.prepareStatement("INSERT INTO favorites (username, trackID) VALUES (?, ?)");
			stmt.setString(1, username);
			stmt.setString(2, trackID);
			stmt.executeUpdate();
		}
		con.close();
	}
	
	public static ArrayList<String> getFavorites(DBConfig dbconfig, String username) throws SQLException {

		Connection con = getConnection(dbconfig);
		
		if(!tableExists(con, "favorites")) {
			createFavoritesTable(con);
		}
		
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("SELECT * FROM favorites WHERE username = ?");
		stmt.setString(1, username);
		ResultSet resultSet = stmt.executeQuery();
		
		ArrayList<String> arrayList = new ArrayList<>();
		
		while(resultSet.next()) {
			arrayList.add(resultSet.getString("trackID"));
		}
		
		con.close();
		
		return arrayList;
	}
	
	public static String[] getArtist(DBConfig dbconfig, String artist) throws SQLException {
		Connection con = getConnection(dbconfig);
		
		String[] artistInfo = new String[4];
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("SELECT * FROM artist WHERE name = ?");
		stmt.setString(1, artist);
		ResultSet resultSet = stmt.executeQuery();
		if(resultSet.next()) {
			artistInfo[0] = resultSet.getString("name");
			artistInfo[1] = resultSet.getString("listeners");
			artistInfo[2] = resultSet.getString("playcount");
			artistInfo[3] = resultSet.getString("bio");
			return artistInfo;
		}
		
		return null;
	}
	
	public static ArrayList<String> getArtists(DBConfig dbconfig, String sortBy) throws SQLException {
		Connection con = getConnection(dbconfig);
		
		ArrayList<String> artists = new ArrayList<>();
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("SELECT * FROM artist ORDER BY ? ASC");
		stmt.setString(1, sortBy);
		ResultSet resultSet = stmt.executeQuery();
		
		while(resultSet.next()) {
			artists.add(resultSet.getString("name"));
		}
		
		return artists;
	}
	
	public static boolean deleteFavorite(DBConfig dbconfig, String user, String trackId) throws SQLException {
		Connection con = getConnection(dbconfig);
		
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("DELETE FROM favorites WHERE username = ? AND trackID = ? ");
		stmt.setString(1, user);
		stmt.setString(2, trackId);
		int resultSet = stmt.executeUpdate();
		if(resultSet > 0)
			return true;
		return false;
	}
}
