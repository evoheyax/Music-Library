package MusicLibrary;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ConcurrentMusicLibrary extends MusicLibrary {

//TODO: instance variables should be private! ---- done ----
	private ReentrantLock rl;
	
	ConcurrentMusicLibrary() {
		super();
		this.rl = new ReentrantLock();
	}
	
	public void addSong(Song song) {
		this.rl.lockWrite();
		super.addSong(song);
		this.rl.unlockWrite();
	}
	
	public void export(String order, Path savePath) {
		this.rl.lockRead();
		super.export(order, savePath);
		this.rl.unlockRead();
	}
	
	public JSONObject searchByArtist(String artist) {
		this.rl.lockRead();
		try {
			return super.searchByArtist(artist);
		} finally {
			this.rl.unlockRead();
		}
	}
	
	public JSONObject searchByTitle(String title) {
		this.rl.lockRead();
		try {
			return super.searchByTitle(title);
		} finally {
			this.rl.unlockRead();
		}
	}
	
	public JSONObject searchByTag(String tag) {
		this.rl.lockRead();
		try {
			return super.searchByTag(tag);
		} finally {
			this.rl.unlockRead();
		}
	}
	
	public Song getSongById(String trackId) {
		this.rl.lockRead();
		try {
			return super.getSongById(trackId);
		} finally {
			this.rl.unlockRead();
		}
	}
	
	public boolean containsId(String trackId) {
		this.rl.lockRead();
		try {
			return super.containsId(trackId);
		} finally {
			this.rl.unlockRead();
		}
	}
	
	public JSONArray getSongsAlpha() {
		this.rl.lockRead();
		try {
			return super.getSongsAlpha();
		} finally {
			this.rl.unlockRead();
		}
	}
	
	public ArrayList<String> getArtistsAlpha() {
		this.rl.lockRead();
		try {
			return super.getArtistsAlpha();
		} finally {
			this.rl.unlockRead();
		}
	}
	
	/*public ArrayList<String> exportArtists() {
		this.rl.lockRead();
		try {
			return super.exportArtists();
		} finally {
			this.rl.unlockRead();
		}
	}*/
}
