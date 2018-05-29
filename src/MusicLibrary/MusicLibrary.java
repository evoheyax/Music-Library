package MusicLibrary;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Comparators.ArtistComparator;
import Comparators.StringComparator;
import Comparators.TitleComparator;

/**
 * Maintains a music library of several songs.
 * @author srollins
 *
 */
public class MusicLibrary {

	/**
	 * Declare appropriate instance variables.
	 * It should be easy to retrieve a song given its unique track ID.
	 * It should also be easy to retrieve a sorted set of songs by a given
	 * artist.
	 */
	
	private HashMap<String, Song> idMap;
	private TreeMap<String, TreeSet<Song>> titleMap;
	private TreeMap<String, TreeSet<Song>> artistMap;
	private TreeMap<String, TreeSet<String>> tagMap;
	//private ArrayList<String> artistList;
	
	/**
	 * Constructor
	 */
	public MusicLibrary() {
		this.idMap = new HashMap<String, Song>();
		this.titleMap = new TreeMap<String, TreeSet<Song>>(new StringComparator());
		this.artistMap = new TreeMap<String, TreeSet<Song>>(new StringComparator());
		this.tagMap = new TreeMap<String, TreeSet<String>>(new StringComparator());
		//this.artistList = new ArrayList<>();
	}
	
	/**
	 * Add a song to the library.
	 * Make sure to add a reference to the song object to all 
	 * appropriate data structures.
	 * @param song
	 */
	public void addSong(Song song) {
		
		String title = song.getTitle();
		String artist = song.getArtist();
		String id = song.getTrackId();
		ArrayList<String> tags = song.getTags();
		
		if(!this.titleMap.containsKey(title)) {
			this.titleMap.put(title, new TreeSet<Song>(new TitleComparator()));
		}
		
		TreeSet<Song> titleTmp = this.titleMap.get(title);
		titleTmp.add(song);
		
		if(!this.artistMap.containsKey(artist)) {
			this.artistMap.put(artist, new TreeSet<Song>(new ArtistComparator()));
		}
		
		TreeSet<Song> artistTmp = this.artistMap.get(artist);
		artistTmp.add(song);
		
		for(String tag: tags) {
			if(!this.tagMap.containsKey(tag)) {
				this.tagMap.put(tag, new TreeSet<String>(new StringComparator()));
			}
			
			TreeSet<String> tagTmp = this.tagMap.get(tag);
			tagTmp.add(id);
		}
		
		if(!this.idMap.containsKey(id)) {
			this.idMap.put(id, song);
		}
		//if(!this.artistList.contains(artist))
			//this.artistList.add(artist);
	}
	/**
	 * Return a JSONObject of data
	 * about songs similar to the
	 * songs of the artist provided
	 * @param artist
	 * @return
	 */
	
	public JSONObject searchByArtist(String artist) {
		JSONObject byArtistObject = new JSONObject();
		JSONArray songList = new JSONArray();
		TreeSet<String> simSongsList = new TreeSet<>();
		
		if(this.artistMap.get(artist) != null) {
			for(Song song: this.artistMap.get(artist)) {
				for(String simSong: song.getSimilars()) {
					if(idMap.containsKey(simSong)) {
						simSongsList.add(simSong);
					}
				}
			}
			for(String trackId: simSongsList) {
				songList.add(idMap.get(trackId).toJSON());
			}
		}
		
		byArtistObject.put("artist", artist);
		byArtistObject.put("similars", songList);
		
		return byArtistObject;
	}
	
	/**
	 * Return a JSONObject of data
	 * about songs similar to the
	 * songs of the song provided
	 * @param title
	 * @return
	 */
	public JSONObject searchByTitle(String title) {
		JSONObject byTitleObject = new JSONObject();
		JSONArray songList = new JSONArray();
		TreeSet<String> simSongsList = new TreeSet<>();
		
		if(this.titleMap.get(title) != null) {
			for(Song song: this.titleMap.get(title)) {	
				for(String simSong: song.getSimilars()) {
					if(idMap.containsKey(simSong)) {
						simSongsList.add(simSong);
					}
				}
			}
			for(String trackId: simSongsList) {
				songList.add(idMap.get(trackId).toJSON());
			}
		}
		
		byTitleObject.put("title", title);
		byTitleObject.put("similars", songList);
		
		return byTitleObject;
	}
	
	
	/**
	 * Returns a JSONObject of data
	 * related to the search tag
	 * @param tag
	 * @return
	 */
	public JSONObject searchByTag(String tag) {
		JSONObject bySearchObject = new JSONObject();
		JSONArray songList = new JSONArray();
		
		if(this.tagMap.get(tag) != null) {
			for(String currTag: this.tagMap.get(tag)) {
				songList.add(idMap.get(currTag).toJSON());
			}
		}
		
		bySearchObject.put("tag", tag);
		bySearchObject.put("similars", songList);
		
		return bySearchObject;
	}
	
	/**
	 * Export the correct map to correct path.
	 * @param order - the map associated with a particular sort order
	 * @param savePath - the location of where the map should be saved
	 */
	
	public void export(String order, Path savePath) {
		switch(order) {
			case "artist":
				this.exportSongsByTitleOrArtist(order, savePath);
				break;
			case "title":
				this.exportSongsByTitleOrArtist(order, savePath);
				break;
			case "tag":
				this.exportSongsByTag(savePath);
		}
	}
	
	/**
	 * Export the title or artist map to a file.
	 * @param order - the map associated with a particular sort order: either title or artist
	 * @param savePath - the location of where the map should be saved
	 */
	
	private void exportSongsByTitleOrArtist(String order, Path savePath) {
		TreeMap<String, TreeSet<Song>> libraryMap;
		if(order.equals("title")) {
			libraryMap = this.titleMap;
		} else {
			libraryMap = this.artistMap;
		}
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(savePath, Charset.forName("UTF-8")))) {
			for(String key: libraryMap.navigableKeySet()) {
				for(Song song: libraryMap.get(key)) {
					writer.println(song.getArtist()+" - "+song.getTitle());	
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to write to file on path: " + savePath.toString());
		}
	}
	
	/**
	 * Export the tag map to a file.
	 * @param savePath - the location of where the tag map should be saved
	 */
	
	private void exportSongsByTag(Path savePath) {
		TreeMap<String, TreeSet<String>> libraryMap = this.tagMap;
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(savePath, Charset.forName("UTF-8")))) {
			for(String key: libraryMap.navigableKeySet()) {
				String keyLine = key+": ";
				for(String tag: libraryMap.get(key)) {
					keyLine += tag+" ";
				}
				writer.println(keyLine);	
			}
		} catch (IOException e) {
			System.err.println("Failed to write to file on path: " + savePath.toString());
		}
	}
	
	public JSONArray getSongsAlpha() {
		TreeMap<String, TreeSet<Song>> libraryMap;
		libraryMap = this.titleMap;
		JSONArray songs = new JSONArray();
		for(String key: libraryMap.navigableKeySet()) {
			for(Song song: libraryMap.get(key)) {
				songs.add(song.toJSON());
			}
		}
		return songs;
	}
	
	public ArrayList<String> getArtistsAlpha() {
		TreeMap<String, TreeSet<Song>> libraryMap;
		libraryMap = this.artistMap;
		ArrayList<String> songs = new ArrayList<String>();
		for(String key: libraryMap.navigableKeySet()) {
			for(Song song: libraryMap.get(key)) {
				if(!songs.contains(song.getArtist()))
					songs.add(song.getArtist());
			}
		}
		return songs;
	}
	
	public Song getSongById(String trackId) {
		Song tmp = this.idMap.get(trackId);
		Song song = new Song(tmp.getArtist(), tmp.getTrackId(), tmp.getTitle(), null, null);
		return song;
	}
	
	public boolean containsId(String trackId) {
		if(this.idMap.containsKey(trackId))
			return true;
			
		return false;
	}
	
	/*public ArrayList<String> exportArtists() {
		return this.artistList;
	}*/
}
