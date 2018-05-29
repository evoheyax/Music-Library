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

public class SongServlet extends BaseServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
HttpSession session = request.getSession();
		
		String page = "song";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		String trackId = request.getParameter("trackId") != null?request.getParameter("trackId"):null;
		
		ConcurrentMusicLibrary cmlb = (ConcurrentMusicLibrary) request.getServletContext().getAttribute("musiclibrary");
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		
		if(trackId != null) {
			if(cmlb.containsId(trackId)) {
				Song song = cmlb.getSongById(trackId);
				JSONObject results = cmlb.searchByTitle(song.getTitle());
				JSONArray similars = (JSONArray) results.get("similars");
				
				// Build favorites list
				ArrayList<String> favorites = null;
				try {
					favorites = DBHelper.getFavorites(dbConfig, username);
				} catch (SQLException e) {
					System.err.println("Error: Favotries List could not be retrieved!");
				}
				
				// Create HTML Parser
				HtmlParser htmlParser = new HtmlParser(page, username, type, query);
				htmlParser.setSongInfo(song);
				htmlParser.createSearchTable(favorites, similars, trackId);
				
				PrintWriter writer = prepareResponse(response);
				writer.println(htmlParser.getHtml());
			} else {
				response.sendRedirect(response.encodeRedirectURL("/search"));
			}
		} else {
			response.sendRedirect(response.encodeRedirectURL("/search"));
		}
	}
}
