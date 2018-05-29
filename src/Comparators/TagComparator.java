package Comparators;
import java.util.Comparator;

import MusicLibrary.Song;

public class TagComparator implements Comparator<Song> {

	@Override
	public int compare(Song o1, Song o2) {
		String trackId1 = o1.getTrackId();
		String trackId2 = o2.getTrackId();

		return trackId1.compareTo(trackId2);
	}
}
