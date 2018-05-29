package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Html.HtmlParser;

public class SearchServlet extends BaseServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String page = "search";
		String username = session.getAttribute("user") != null?session.getAttribute("user").toString():"";
		String type = "";
		String query = "";
		
		HtmlParser htmlParser = new HtmlParser(page, username, type, query);
		
		PrintWriter writer = prepareResponse(response);
		writer.println(htmlParser.getHtml());
	}
}
