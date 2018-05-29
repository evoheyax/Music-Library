package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Html.HtmlParser;
import Util.DBConfig;
import Util.DBHelper;

public class LoginServlet extends BaseServlet {
public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String page = "login";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		

		String user = request.getParameter("username") != null?request.getParameter("username"):"";
		String pass = request.getParameter("password") != null?request.getParameter("password"):"";
		String error = null;
		boolean match = false;
		
		DBConfig dbConfig = (DBConfig) request.getServletContext().getAttribute("dbConfig");
		
		try {
			match = DBHelper.validateLogin(dbConfig, user, pass);
		} catch (SQLException e) {
			System.err.println("Error validating user for login!");
		}
		
		if(user != null && user.equals("") || pass != null && pass.equals("")) {
			error = "Error: Please fill out all lines of the form!";
		} else if(!match) {
			error = "Error: Username/Password Combination Incorrect";
		} else {
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
		
		String page = "login";
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
