package MusicLibrary;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExtractSongData implements Runnable {
	
	private Path path;
	private MusicLibrary musicLibrary;
	
	ExtractSongData(MusicLibrary musicLibrary, Path path) {
		this.musicLibrary = musicLibrary;
		this.path = path;
	}

	public void run() {
		extractSongData(path);
	}

	private void extractSongData(Path path) {
		JSONParser parser = new JSONParser();
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			JSONObject contents = (JSONObject) parser.parse(reader);
			
			Song song = new Song(contents);
			this.musicLibrary.addSong(song);
		} catch (IOException e) {
			System.err.println("Failed to open file on path: " + path.toString());
		} catch (ParseException e) {
			System.err.println("Failed to parse to JSON object!");
		}
	}
}
