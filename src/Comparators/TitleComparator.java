package Comparators;
import java.util.Comparator;

import MusicLibrary.Song;

public class TitleComparator implements Comparator<Song> {

	@Override
	public int compare(Song o1, Song o2) {
		String artist1 = o1.getArtist();
		String artist2 = o2.getArtist();
		String title1 = o1.getTitle();
		String title2 = o2.getTitle();
		String trackId1 = o1.getTrackId();
		String trackId2 = o2.getTrackId();
		if(!title1.equals(title2)) {
			return title1.compareTo(title2);
		} else if (!artist1.equals(artist2)){
			return artist1.compareTo(artist2);
		} else if (!trackId1.equals(trackId2)){
			return trackId1.compareTo(trackId2);
		}
		return 0;
	}
}
