package MusicLibrary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SongDataParser {

	/**
	 * Declare appropriate data members here.
	 * Remember to make sure they are declared private unless there is a good
	 * reason to use public, package, or protected!
	 */

	private ConcurrentMusicLibrary musicLibrary;
	private Path searchPath;
	private String fromFileType;
	private WorkQueue wq;
	
	
	/**
	 * Construct a SongDataParser object by initializing any data members and 
	 * traversing the directory specified by the path parameter.
	 * If isRecursive is true, recursively traverse the directory structure.
	 * If isRecursive is false, traverse only the JSON files found in the 
	 * directory specified by path.  
	 * @param path
	 * @param isRecursive
	 */
	public SongDataParser(Path searchPath, int threads) {
		this.musicLibrary = new ConcurrentMusicLibrary();
		this.searchPath = searchPath;
		this.fromFileType = ".json";
		this.wq = new WorkQueue(threads);
		
		findSongsRecursively(this.searchPath, this.fromFileType);
		
		this.wq.shutdown();
		this.wq.awaitTermination();
		
	}

/**
 * Recursive function to search for songs
 * @param path - the path of the dirctory
 * @param fromFileType - the file type of a file usually .json
 * @param file - an array list of strings which contins that path of the files
 */

	// Recursive function to search for songs
	private void findSongsRecursively(Path path, String fromFileType) {
		if(Files.isDirectory(path)) {
					
			try(DirectoryStream<Path> dir = 
				Files.newDirectoryStream(path)) {
				
				for(Path entry: dir) {
					findSongsRecursively(entry, fromFileType);
				}
				
				
			} catch (IOException e) {
				System.err.println("Failed to open file on path: " + path.toString());
			}
			
			
		} else if(path.toString().endsWith(fromFileType.toLowerCase().trim()) || path.toString().endsWith(fromFileType.toUpperCase().trim())) {
			this.wq.execute(new ExtractSongData(this.musicLibrary, path));
		}
	}
	
	public ConcurrentMusicLibrary getMusicLibrary() {
		return this.musicLibrary;
	}
}
