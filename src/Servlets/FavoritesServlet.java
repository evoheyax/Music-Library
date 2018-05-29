package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import Html.HtmlParser;
import MusicLibrary.ConcurrentMusicLibrary;
import MusicLibrary.Song;
import Util.DBConfig;
import Util.DBHelper;

public class FavoritesServlet extends BaseServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String page = "songs";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		
		// If not logged in, redirect
		if(username == "") {
			response.sendRedirect(response.encodeRedirectURL("/search"));
		}
		
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		ConcurrentMusicLibrary cmlb = (ConcurrentMusicLibrary) request.getServletContext().getAttribute("musiclibrary");
		
		// Get Favorites List
		ArrayList<String> favorites = null;
		try {
			favorites = DBHelper.getFavorites(dbConfig, username);
		} catch (SQLException e) {
			System.err.println("Error: Favotries List could not be retrieved!");
		}
		
		HtmlParser htmlParser = new HtmlParser(page, username, type, query);
		
		htmlParser.createFavoritesTable(favorites, cmlb);
		
		PrintWriter writer = prepareResponse(response);
		writer.println(htmlParser.getHtml());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String trackId = request.getParameter("trackId") != null?request.getParameter("trackId"):null;
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		
		try {
			DBHelper.deleteFavorite(dbConfig, username, trackId);
		} catch (SQLException e) {
			System.err.println("Error: Can not delete Favorite! "+e);
		}
		
		response.sendRedirect(response.encodeRedirectURL("/favorites"));
	}
}
