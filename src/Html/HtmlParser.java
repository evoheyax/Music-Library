package Html;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MusicLibrary.ConcurrentMusicLibrary;
import MusicLibrary.Song;

public class HtmlParser {
	private String page;
	private String user;
	private String type;
	private String query;
	private String path;
	private String searchPath;
	private String viewAllPath;
	protected String html;
	private String searchHtml;
	private String viewAllHtml;
	
	public HtmlParser(String page, String user, String type, String query) {
		this.page = page;
		this.user = user;
		this.type = type;
		this.query = query;
		this.path = "html/pages/"+this.page+".html";
		this.searchPath = "html/searchbar.html";
		this.viewAllPath = "html/viewallbar.html";
		this.html = "";
		this.searchHtml = "";
		this.viewAllHtml = "";
		
		this.getSearchHtml();
		this.getViewAllHtml();
		this.getHtmlPage();
		this.setLoginStatus();
	}
	
	private void setLoginStatus() {
		//long start = System.currentTimeMillis();
		if(user == "") {
			this.html = this.html.replaceAll("INSERT LOGIN HERE", "<a href='/login'>Login</a> | <a href='/signup'>Create Account</a>");
		} else {
			this.html = this.html.replaceAll("INSERT LOGIN HERE", "Welcome back, <a href='/profile'>"+user+"</a> | <a href='/logout'>Logout</a><br /><a href='/favorites'>Favorites List</a>");
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("setLoginStatus() took: "+(stop-start)+" ms");
	}
	
	private void getHtmlPage() {
		//long start = System.currentTimeMillis();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(this.path), Charset.forName("UTF-8"))) {
			String line = reader.readLine();
			while(line != null) {
				if(line.contains("INSERT SEARCH BAR HERE"))
					line = this.searchHtml;
				if(line.contains("INSERT VIEW ALL HERE"))
					line = this.viewAllHtml;
				this.html += line;
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Failed to open file on path: " + this.path);
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("getHtmlPage() took: "+(stop-start)+" ms");
	}
	
	private void getSearchHtml() {
		//long start = System.currentTimeMillis();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(this.searchPath), Charset.forName("UTF-8"))) {
			String line = reader.readLine();
			while(line != null) {
				if(line.contains("INSERT QUERY HERE"))
					line = "<input type=\"text\" name=\"query\" value=\""+this.query+"\">";
				if(this.type.equals("artist") && line.contains("Artist"))
					line = "<option value=\"artist\" selected>Artist</option>";
				if(this.type.equals("title") && line.contains("Title"))
					line = "<option value=\"title\" selected>Title</option>";
				if(this.type.equals("tag") && line.contains("Tag"))
					line = "<option value=\"tag\" selected>Tag</option>";
				this.searchHtml += line;
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Failed to open file on path: " + this.path);
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("getSearchHtml() took: "+(stop-start)+" ms");
	}
	
	private void getViewAllHtml() {
		//long start = System.currentTimeMillis();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(this.viewAllPath), Charset.forName("UTF-8"))) {
			String line = reader.readLine();
			while(line != null) {
				/*if(line.contains("INSERT QUERY HERE"))
					line = "<input type=\"text\" name=\"query\" value=\""+this.query+"\">";
				if(this.allType.equals("artist") && line.contains("Artist"))
					line = "<option value=\"artist\" selected>Artist</option>";
				if(this.allType.equals("title") && line.contains("Title"))
					line = "<option value=\"title\" selected>Title</option>";
				if(this.allType.equals("tag") && line.contains("Tag"))
					line = "<option value=\"tag\" selected>Tag</option>";*/
				this.searchHtml += line;
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Failed to open file on path: " + this.path);
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("getSearchHtml() took: "+(stop-start)+" ms");
	}
	
	public void createFavoritesTable(ArrayList<String> favorites, ConcurrentMusicLibrary cmlb) {
		//long start = System.currentTimeMillis();
		if(favorites.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("<tr><td style='text-align: center; padding: 5px; border: #000 solid 1px;'><b>Artist</b></td><td style='text-align: center; padding: 5px; border-top: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><b>Song Title</b></td><td style='text-align: center; padding: 5px; border-top: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><b>Remove</b></td></tr>");
			//String tableHead = "<tr><td style='text-align: center; padding: 5px; border: #000 solid 1px;'><b>Artist</b></td><td style='text-align: center; padding: 5px; border-top: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><b>Song Title</b></td></tr>";
			//String table = "";
			
			for(String trackId: favorites) {
				Song song = cmlb.getSongById(trackId);
				sb.append("<tr><td style='text-align: center; padding: 5px; border-left: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><a href='/artist?artist="+song.getArtist()+"'/>"+song.getArtist()+"</a></td><td style='text-align: center; padding: 5px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><a href='/song?trackId="+song.getTrackId()+"'>"+song.getTitle()+"</a></td><td style='text-align: center; padding: 5px; border-right: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><form action='favorites' method='post'><input type='hidden' name='trackId' value='"+song.getTrackId()+"'><input type='submit' value='Delete'></form></td></tr>");
			}
			this.html = this.html.replaceAll("INSERT SONG TABLE HERE", sb.toString());
		} else {
			this.html = this.html.replaceAll("INSERT SONG TABLE HERE", "You don't have any favorite songs yet!");
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("createFavoritesTable() took: "+(stop-start)+" ms");
	}
	
	public void createSearchTable(ArrayList<String> favorites, JSONArray similars, String fromTrackId) {
		//long start = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		String favoritesColumn = !this.user.equals("")?"<td style='text-align: center; padding: 5px; border-right: #000 solid 1px; border-top: #000 solid 1px; border-bottom: #000 solid 1px;'><b>Favorites</b></td>":"";
		
		sb.append("<tr><td style='text-align: center; padding: 5px; border: #000 solid 1px;'><b>Artist</b></td><td style='text-align: center; padding: 5px; border-top: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><b>Song Title</b></td>"+favoritesColumn+"</tr>");
		
		Iterator<JSONObject> similarsIterator = similars.iterator();
		if(similarsIterator.hasNext()) {
			while (similarsIterator.hasNext()) {
				JSONObject song = similarsIterator.next();
				
				sb.append("<tr><td style='text-align: center; padding: 5px; border-left: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><a href='/artist?artist="+song.get("artist")+"'/>"+song.get("artist")+"</a></td><td style='text-align: center; padding: 5px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><a href='/song?trackId="+song.get("trackId")+"'>"+song.get("title")+"</a></td>");
				String favoritesCode = (favorites != null && favorites.contains(song.get("trackId")))?"Liked!":"<form action='songs' method='post'><input type='hidden' name='from' value='"+this.page+"'><input type='hidden' name='fromTrackId' value='"+fromTrackId+"'><input type='hidden' name='trackId' value='"+song.get("trackId")+"'><input type='hidden' name='type' value='"+type+"'><input type='hidden' name='query' value=\""+query+"\"><input type='submit' value='Add to Favorite'></form>";
				sb.append(!this.user.equals("")?"<td style='text-align: center; padding: 5px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'>"+favoritesCode+"</td>":"");
				sb.append("</tr>");
			}
			this.html = this.html.replaceAll("INSERT SONG TABLE HERE",  Matcher.quoteReplacement(sb.toString()));
		} else {
			this.html = this.html.replaceAll("INSERT SONG TABLE HERE", "No Results Found. Please try again!");
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("createSearchTable() took: "+(stop-start)+" ms");
	}
	
	public void createArtistTable(ArrayList<String> artists) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><td style='text-align: center; padding: 5px; border: #000 solid 1px;'><b>Artist</b></td></tr>");
		
		for(String artist: artists) {
			sb.append("<tr><td style='text-align: center; padding: 5px; border-left: #000 solid 1px; border-right: #000 solid 1px; border-bottom: #000 solid 1px;'><a href='/artist?artist="+artist+"'/>"+artist+"</a><td></tr>");
		}
		
		this.html = this.html.replaceAll("INSERT SONG TABLE HERE",  Matcher.quoteReplacement(sb.toString()));
	}
	
	public void setSongInfo(Song song) {
		this.html = this.html.replaceAll("INSERT SONG NAME HERE", song.getTitle()).replaceAll("INSERT TITLE HERE", song.getTitle()).replaceAll("INSERT ARTIST HERE", "<a href='/artist?artist="+song.getArtist()+"'/>"+song.getArtist()+"</a>");;
	}
	
	public void setArtistInfo(String[] artistInfo) {
		this.html = this.html.replaceAll("INSERT ARTIST HERE", (artistInfo[0] != null)?artistInfo[0]:"Unknown").replaceAll("INSERT LISTENERS HERE", (artistInfo[1] != null)?artistInfo[1]:"Unknown").replaceAll("INSERT PLAY COUNT HERE", (artistInfo[2] != null)?artistInfo[2]:"Unknown").replaceAll("INSERT BIO HERE", (artistInfo[3] != null)?artistInfo[3]:"Unknown");
	}
	
	public void setError(String error) {
		this.html = this.html.replaceAll("INSERT ERROR HERE", "<font color='red'>"+error+"</font>");
	}
	
	public String getHtml() {
		return this.html;
	}
}
