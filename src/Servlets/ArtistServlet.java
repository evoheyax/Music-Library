package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Html.HtmlParser;
import MusicLibrary.ConcurrentMusicLibrary;
import Util.DBConfig;
import Util.DBHelper;

public class ArtistServlet extends BaseServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String page = "artist";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		String artist = request.getParameter("artist") != null?request.getParameter("artist"):null;
		
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		
		// Get Artist Info
		String[] artistInfo = null;
		if(artist != null) {
			try {
				artistInfo = DBHelper.getArtist(dbConfig, artist);
			} catch (SQLException e) {
				System.err.println("Error: Can not add favorite to list!");
			}
		}
		
		if(artistInfo != null) {
			// Parse HTML
			HtmlParser htmlParser = new HtmlParser(page, username, type, query);
			htmlParser.setArtistInfo(artistInfo);
			
			PrintWriter writer = prepareResponse(response);
			writer.println(htmlParser.getHtml());
		}  else {
			response.sendRedirect(response.encodeRedirectURL("/search"));
		}
	}
}
