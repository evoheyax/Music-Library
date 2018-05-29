package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Html.HtmlParser;
import MusicLibrary.ConcurrentMusicLibrary;
import MusicLibrary.Song;
import Util.DBConfig;
import Util.DBHelper;

public class ViewAllServlet extends BaseServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		String page = "viewall";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		String sortBy = request.getParameter("sortBy") != null?request.getParameter("sortBy"):null;
		
		ConcurrentMusicLibrary cmlb = (ConcurrentMusicLibrary) request.getServletContext().getAttribute("musiclibrary");
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		
		// Start Parsing HTML
		HtmlParser htmlParser = new HtmlParser(page, username, type, query);
		
		if(sortBy != null && sortBy.equals("titleByAlpha")) { // sort titles
			// Get Songs Aphabetically Sorted
			JSONArray songs = cmlb.getSongsAlpha();
			
			// Build favorites list
			ArrayList<String> favorites = null;
			try {
				favorites = DBHelper.getFavorites(dbConfig, username);
			} catch (SQLException e) {
				System.err.println("Error: Favotries List could not be retrieved!");
			}
			
			htmlParser.createSearchTable(favorites, songs, "");
		} else { // sort artists
			ArrayList<String> artists = null;
			switch(sortBy) {
				case "artistByAlpha":
					artists = cmlb.getArtistsAlpha();
				break;
				case "artistByPlayCount":
					try {
						artists = DBHelper.getArtists(dbConfig, "playcount");
					} catch (SQLException e) {
						System.err.println("Error: cannot get Artist List by play count!");
					}
				break;
				case "artistByListeners":
					try {
						artists = DBHelper.getArtists(dbConfig, "listeners");
					} catch (SQLException e) {
						System.err.println("Error: cannot get Artist List by play count!");
					}
				break;
			}
			htmlParser.createArtistTable(artists);
		}
		
		// Write out HTML
		PrintWriter writer = prepareResponse(response);
		writer.println(htmlParser.getHtml());
	}
}
