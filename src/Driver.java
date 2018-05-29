import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MusicLibrary.ConcurrentMusicLibrary;
import MusicLibrary.MusicLibrary;
import MusicLibrary.SongDataParser;
import Util.CommandLineParser;
import Util.ExtractSearchTest;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;

public class Driver {
	
	public static void main(String[] args) {

		CommandLineParser cmp = new CommandLineParser(args);	
		if(cmp.testArgs()) {
			SongDataParser sdp = new SongDataParser(cmp.getInput(), cmp.getThreads());
			
			ConcurrentMusicLibrary mlb = sdp.getMusicLibrary();
			
			mlb.export(cmp.getOrder(), cmp.getOutput());
			
			if(cmp.getSearchInput() != null) {
				ExtractSearchTest est = new ExtractSearchTest(mlb, cmp.getSearchInput(), cmp.getSearchOutput());
				
				est.exportData();
			}
		}
		
	}

}
