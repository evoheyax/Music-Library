package Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import MusicLibrary.ConcurrentMusicLibrary;

public class ExtractSearchTest {
	private ConcurrentMusicLibrary mlb;
	private Path input;
	private Path output;
	private JSONObject inputObject;
	private JSONObject outputObject;
	private JSONArray artistsArray;
	private JSONArray titlesArray;
	private JSONArray tagsArray;
	
	public ExtractSearchTest(ConcurrentMusicLibrary mlb, Path input, Path output) {
		this.mlb = mlb;
		this.input = input;
		this.output = output;
		
		parseInput();
		
		extractArtistData(getArtists());
		extractTitleData(getTitles());
		extractTagData(getTags());
	}
	
	private void extractArtistData(JSONArray artists) {
		if(artists != null) {
			this.artistsArray = new JSONArray();
			Iterator<String> artistIterator = artists.iterator();
			while (artistIterator.hasNext()) {
				this.artistsArray.add(mlb.searchByArtist(artistIterator.next()));
			}
		}  else {
			this.artistsArray = null;
		}
	}
	
	private void extractTitleData(JSONArray titles) {
		if(titles != null) {
			this.titlesArray = new JSONArray();
			Iterator<String> titleIterator = titles.iterator();
			while (titleIterator.hasNext()) {
				this.titlesArray.add(mlb.searchByTitle(titleIterator.next()));
			}
		} else {
			this.titlesArray = null;
		}
	}
	
	private void extractTagData(JSONArray tags) {
		if(tags != null) {
			this.tagsArray = new JSONArray();
			Iterator<String> tagIterator = tags.iterator();
			while (tagIterator.hasNext()) {
				this.tagsArray.add(mlb.searchByTag(tagIterator.next()));
			}
		} else {
			this.tagsArray = null;
		}
	}
	
	private JSONArray getArtists() {
		if(inputObject == null || !inputObject.containsKey("searchByArtist")) {
			return null;
		}
		return (JSONArray) inputObject.get("searchByArtist");
	}
	
	private JSONArray getTitles() {
		if(inputObject == null || !inputObject.containsKey("searchByTitle")) {
			return null;
		}
		return (JSONArray) inputObject.get("searchByTitle");
	}
	
	private JSONArray getTags() {
		if(inputObject == null || !inputObject.containsKey("searchByTag")) {
			return null;
		}
		return (JSONArray) inputObject.get("searchByTag");
	}
	
	private void parseInput() {
		JSONParser parser = new JSONParser();
		try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"))) {
			inputObject = (JSONObject) parser.parse(reader);
		} catch (IOException e) {
			System.err.println("Failed to open file on path: " + input.toString());
		} catch (ParseException e) {
			System.err.println("Failed to parse to JSON object!");
		}
	}
	
	public void exportData() {
		JSONObject finalObject = new JSONObject();
		if(artistsArray != null) {
			finalObject.put("searchByArtist", artistsArray);
		}
		if(titlesArray != null) {
			finalObject.put("searchByTitle", titlesArray);
		}
		if(tagsArray != null) {
			finalObject.put("searchByTag", tagsArray);
		}
		
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(this.output, Charset.forName("UTF-8")))) {
			writer.println(finalObject);
		} catch (IOException e) {
			System.err.println("Failed to write to file on path: " + this.output.toString());
		}
	}
}
