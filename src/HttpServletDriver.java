import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import MusicLibrary.ConcurrentMusicLibrary;
import MusicLibrary.SongDataParser;
import Servlets.ArtistServlet;
import Servlets.FavoritesServlet;
import Servlets.LoginServlet;
import Servlets.LogoutServlet;
import Servlets.ProfileServlet;
import Servlets.SearchServlet;
import Servlets.SignupServlet;
import Servlets.SongServlet;
import Servlets.SongsServlet;
import Servlets.ViewAllServlet;
import Util.DBConfig;

public class HttpServletDriver {
	private static final int DEFAULT_PORT = 14051;
	private static final Path INPUT_PATH = Paths.get("input/lastfm_subset");
	private static final int THREADS = 5;
	private static final DBConfig dbConfig = new DBConfig("user25", "user25", "user25", "sql.cs.usfca.edu", "3306");
			
	public static void main(String[] args) throws Exception { //<- not recommended to throw Exception in general, but hard to avoid in this case
		Server server = new Server(DEFAULT_PORT);

		//create a ServletHander to attach servlets
		ServletContextHandler servhandler = new ServletContextHandler(ServletContextHandler.SESSIONS);        
		server.setHandler(servhandler);

		servhandler.addEventListener(new ServletContextListener() {

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				//Do nothing when server shut down.
			}

			@Override
			public void contextInitialized(ServletContextEvent sce) {

				SongDataParser sdp = new SongDataParser(INPUT_PATH, THREADS);
				
				ConcurrentMusicLibrary mlb = sdp.getMusicLibrary();
				sce.getServletContext().setAttribute("musiclibrary", mlb);
				sce.getServletContext().setAttribute("dbConfig", dbConfig);
			}

		});

		//add a servlet for searching for search
		servhandler.addServlet(SearchServlet.class, "/search");
		
		//add a servlet for songs
		servhandler.addServlet(SongsServlet.class, "/songs");
		
		//add a servlet for song
		servhandler.addServlet(SongServlet.class, "/song");
		
		//add a servlet for artist
		servhandler.addServlet(ArtistServlet.class, "/artist");
		
		//add a servlet for view all
		servhandler.addServlet(ViewAllServlet.class, "/viewall");
		
		//add a servlet for favorites
		servhandler.addServlet(FavoritesServlet.class, "/favorites");
		
		//add a servlet for profile
		servhandler.addServlet(ProfileServlet.class, "/profile");
		
		//add a servlet for signup
		servhandler.addServlet(SignupServlet.class, "/signup");
		
		//add a servlet for login
		servhandler.addServlet(LoginServlet.class, "/login");
		
		//add a servlet for logout
		servhandler.addServlet(LogoutServlet.class, "/logout");
		
		//add a servlet everything else
		servhandler.addServlet(SearchServlet.class, "/*");
		
		//set the list of handlers for the server
		server.setHandler(servhandler);

		server.start();
		server.join();
	}
}
