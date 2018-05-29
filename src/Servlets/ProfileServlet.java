package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Html.HtmlParser;
import Util.DBConfig;

public class ProfileServlet extends BaseServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String page = "profile";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		
		// Parse HTML
		HtmlParser htmlParser = new HtmlParser(page, username, type, query);
		
		PrintWriter writer = prepareResponse(response);
		writer.println(htmlParser.getHtml());
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String trackId = request.getParameter("trackId") != null?request.getParameter("trackId"):null;
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
	}
}
