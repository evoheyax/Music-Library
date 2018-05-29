import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import MusicLibrary.ConcurrentMusicLibrary;
import MusicLibrary.SongDataParser;
import Util.DBConfig;
import Util.DBHelper;
import Util.HTTPFetcher;

public class ExtractLastFMData {
	private static final DBConfig dbConfig = new DBConfig("user25", "user25", "user25", "127.0.0.1", "3306");
	
	public static void main(String args[]) {
		SongDataParser sdp = new SongDataParser(Paths.get("input/lastfm_subset/"), 10);
		
		ConcurrentMusicLibrary mlb = sdp.getMusicLibrary();
		
		ArrayList<String> artistList = mlb.exportArtists();
		
		System.out.println(artistList.size());
		//fetchAndStoreArtists(artistList, dbConfig);
	}
	
	public static void fetchAndStoreArtists(ArrayList<String> artists, DBConfig dbconfig) {
		int start = 296;
		int curr = 0;
		for(String inArtist: artists) {
			String artist = null;
			int listeners = 0;
			int playCount = 0;
			String bio = null;
			
			if(curr >= start) {
				String jsonObject = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo&artist="+inArtist+"&api_key=27afa3baecea5827d6bdbe7d4ae91c5a&format=json").split("\\n\\s")[1];
				
				JSONObject artistInfo = null;
				JSONParser parser = new JSONParser();
				try {
					artistInfo = (JSONObject) parser.parse(jsonObject);
				} catch (ParseException e) {
					System.out.println("Error Parsing! "+e.getMessage());
				}
				System.out.println("Looking up artist: "+inArtist);
				if(artistInfo != null && !artistInfo.containsKey("error")) {
					if(artistInfo.containsKey("artist")) {
						JSONObject artistObject = (JSONObject) artistInfo.get("artist");
						artist = (String) artistObject.get("name");
						
						if(artistObject.containsKey("stats")) {
							JSONObject statsObject = (JSONObject) artistObject.get("stats");
							
							if(statsObject.containsKey("listeners"))
								listeners = Integer.parseInt((String) statsObject.get("listeners"));
	
							if(statsObject.containsKey("playcount"))
								playCount = Integer.parseInt((String) statsObject.get("playcount"));
						}
						if(artistObject.containsKey("bio")) {
							JSONObject bioObject = (JSONObject) artistObject.get("bio");
							
							if(bioObject.containsKey("summary"))
							bio = (String) bioObject.get("summary");
						}
						
						try {
							DBHelper.addArtist(dbconfig, artist, listeners, playCount, bio);
						} catch (SQLException e) {
							System.err.println("SQL error while adding atrist to database! "+e.getMessage());
						}
					}
				}
				try {
				    Thread.sleep(200);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			curr++;
		}

	}
}
