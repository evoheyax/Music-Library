package MusicLibrary;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * A class to maintain data about a single song.
 * Java object representation of a JSON object with schema below.
 * @author srollins
 *
 */

/*
{  
   "artist":"The Primitives",
   "timestamp":"2011-09-07 12:34:34.851502",
   "similars":[  
      [  
         "TROBUDC128F92F7F0B",
         1
      ],
      [  
         "TRWSCCK128F92F7EDB",
         0.98714400000000002
      ]
   ],
   "tags":[  
      [  
         "1980s",
         "100"
      ],
      [  
         "80s",
         "33"
      ],
      [  
         "pop",
         "33"
      ],
      [  
         "alternative",
         "33"
      ]
   ],
   "track_id":"TRBDCAB128F92F7EE4",
   "title":"Never Tell"
} 
 
*/

public class Song {

	/**
	 * Declare appropriate instance variables.
	 */
	
	private String artist;
	private String trackId;
	private String title;
	private ArrayList<String> similars;
	private ArrayList<String> tags;
	
	/**
	 * Constructor.
	 * @param artist
	 * @param trackId
	 * @param title
	 * @param similars
	 * @param tags
	 */
	public Song(String artist, String trackId, String title, ArrayList<String> similars, ArrayList<String> tags) {
		this.artist = artist;
		this.trackId = trackId;
		this.title = title;
		this.similars = similars;
		this.tags = tags;
	}

	/**
	 * Constructor that takes as input a JSONObject as illustrated in the example above and
	 * constructs a Song object by extract the relevant data.
	 * @param object
	 */
	public Song(JSONObject object) {
		this.artist = (String) object.get("artist");
		this.trackId = (String) object.get("track_id");
		this.title = (String) object.get("title");
		
		this.similars = new ArrayList<String>();
		JSONArray jsimilars = (JSONArray) object.get("similars");
		if(jsimilars != null) {
			for(int i=0; i<jsimilars.size(); i++) {
				this.similars.add(((JSONArray) jsimilars.get(i)).get(0).toString());
			}
		}
		
		this.tags = new ArrayList<String>();
		JSONArray jtags = (JSONArray) object.get("tags");
		if(jtags != null) {
			for(int i=0; i<jtags.size(); i++) {
				this.tags.add(((JSONArray) jtags.get(i)).get(0).toString());
			}
		}
	}
	
	/**
	 * Return artist.
	 * @return
	 */
	public String getArtist() {
		return this.artist;
	}

	/**
	 * Return track ID.
	 * @return
	 */
	public String getTrackId() {
		return this.trackId;
	}

	/**
	 * Return title.
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Return a list of the track IDs of all similar tracks.
	 * @return
	 */
	public ArrayList<String> getSimilars() {
		return this.similars;
	}

	/**
	 * Return a list of all tags for this track.
	 * @return
	 */
	public ArrayList<String> getTags() {
		return this.tags;
	}
	
	public JSONObject toJSON() {
		JSONObject jsonOb = new JSONObject();
		jsonOb.put("artist", this.artist);
		jsonOb.put("trackId", this.trackId);
		jsonOb.put("title", this.title);
		return jsonOb;
	}
}
