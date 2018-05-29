package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Html.HtmlParser;
import MusicLibrary.ConcurrentMusicLibrary;
import Util.DBConfig;
import Util.DBHelper;

public class SignupServlet extends BaseServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String page = "signup";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		
		String name = request.getParameter("name") != null?request.getParameter("name"):"";
		String user = request.getParameter("username") != null?request.getParameter("username"):"";
		String password1 = request.getParameter("password1") != null?request.getParameter("password1"):"";
		String password2 = request.getParameter("password2") != null?request.getParameter("password2"):"";
		String error = "";
		
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		
		boolean userExists = true;
		try {
			userExists = DBHelper.userExists(dbConfig, user);
		} catch (SQLException e) {
			System.err.println("Error validating user for signup!");
		}
		
		if(name != null && name.equals("") || user != null && user.equals("") || password1 != null && password1.equals("") || password2 != null && password2.equals("")) {
			error = "Error: Please fill out all lines of the form!";
		} else if(userExists) {
			error = "Error: Username is already in use!";
		} else if(!password1.equals(password2)) {
			error = "Error: Passwords do not match!";
		} else {
			try {
				DBHelper.addUser(dbConfig, name, user, password1);
			} catch (SQLException e) {
				System.err.println("Error ading user to databse!");
			}
			
			session.setAttribute("user", user);
			
			response.sendRedirect(response.encodeRedirectURL("/search"));
		}
		
		HtmlParser htmlParser = new HtmlParser(page, username, type, query);
		
		htmlParser.setError(error);
		
		PrintWriter writer = prepareResponse(response);
		writer.println(htmlParser.getHtml());
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String page = "signup";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		
		if(!username.equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/search"));
		}
		
		HtmlParser htmlParser = new HtmlParser(page, username, type, query);
		
		htmlParser.setError("");
		
		PrintWriter writer = prepareResponse(response);
		writer.println(htmlParser.getHtml());
	}
}
