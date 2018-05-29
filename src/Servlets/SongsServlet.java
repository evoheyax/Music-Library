package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

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

public class SongsServlet extends BaseServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		String page = "songs";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = request.getParameter("type");
		String query = request.getParameter("query");
		String trackId = request.getParameter("trackId") != null?request.getParameter("trackId"):null;
		
		ConcurrentMusicLibrary cmlb = (ConcurrentMusicLibrary) request.getServletContext().getAttribute("musiclibrary");
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		
		JSONObject results = null;
		
		// If favorite needs to be added, then add it
		if(trackId != null) {
			try {
				DBHelper.addFavorite(dbConfig, username, trackId);
			} catch (SQLException e) {
				System.err.println("Error: Can not add favorite to list!");
			}
		}
		
		String from = request.getParameter("from") != null?request.getParameter("from"):null;
		String fromTrackId = request.getParameter("fromTrackId") != null?request.getParameter("fromTrackId"):null;
		if(from != null && from.equals("song")) {
			response.sendRedirect(response.encodeRedirectURL("/song?trackId="+fromTrackId));
		} else if(from != null && from.equals("viewall")) {
			response.sendRedirect(response.encodeRedirectURL("/viewall?sortBy=titleByAlpha"));
		} else {
		
			// Build favorites list
			ArrayList<String> favorites = null;
			try {
				favorites = DBHelper.getFavorites(dbConfig, username);
			} catch (SQLException e) {
				System.err.println("Error: Favotries List could not be retrieved!");
			}
			
			// Do the search and get results
			switch(type) {
				case "artist":
					results = cmlb.searchByArtist(query);
				break;
				case "title":
					results = cmlb.searchByTitle(query);
				break;
				case "tag":
					results = cmlb.searchByTag(query);
				break;
			}
			JSONArray similars = (JSONArray) results.get("similars");
			
			// Create HTML Parser
			HtmlParser htmlParser = new HtmlParser(page, username, type, query);
			htmlParser.createSearchTable(favorites, similars, "N/A");
			
			PrintWriter writer = prepareResponse(response);
			writer.println(htmlParser.getHtml());
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.sendRedirect(response.encodeRedirectURL("/search"));
		
	}
}
