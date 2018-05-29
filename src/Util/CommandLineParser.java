package Util;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class CommandLineParser {
	private HashMap<String, String> args;
	
	public CommandLineParser(String[] args) {
		this.args = new HashMap<>();
		this.args.put("threads", "10");
		for(int i=0; i<args.length; i++) {
			if(args[i].equals("-input")) {
				if(args[i+1] != null) {
					this.args.put("input", args[i+1]);
				}
			} else if(args[i].equals("-output")) {
				if(args[i+1] != null) {
					this.args.put("output", args[i+1]);
				}
			} else if(args[i].equals("-order")) {
				if(args[i+1] != null) {
					this.args.put("order", args[i+1]);
				}
			} else if(args[i].equals("-threads")) {
				if(args[i+1] != null) {
					if(isInteger(args[i+1]) && Integer.parseInt(args[i+1]) >= 1 && Integer.parseInt(args[i+1]) <= 1000) {
						this.args.put("threads", args[i+1]);
					}
				}
			} else if(args[i].equals("-searchInput")) {
				if(args[i+1] != null) {
					this.args.put("searchInput", args[i+1]);
				}
			} else if(args[i].equals("-searchOutput")) {
				if(args[i+1] != null) {
					this.args.put("searchOutput", args[i+1]);
				}
			}
		}
	}
	
	public Path getInput() {
		return Paths.get(this.args.get("input"));
	}
	
	public Path getOutput() {
		return Paths.get(this.args.get("output"));
	}
	
	public String getOrder() {
		return this.args.get("order");
	}
	
	public int getThreads() {
		return Integer.parseInt(this.args.get("threads"));
	}
	
	public Path getSearchInput() {
		if(this.args.get("searchInput") == null) {
			return null;
		}
		return Paths.get(this.args.get("searchInput"));
	}
	
	public Path getSearchOutput() {
		if(this.args.get("searchOutput") == null) {
			return null;
		}
		return Paths.get(this.args.get("searchOutput"));
	}
	
	public boolean testArgs() {
		File input = new File(this.args.get("input")!=null?this.args.get("input"):"");
		//File searchInput = new File(this.args.get("searchInput")!=null?this.args.get("searchInput"):"");
		//File searchOutput = new File(this.args.get("searchOutput")!=null?this.args.get("searchOutput"):"");
		//File output = new File(this.args.get("output")!=null?this.args.get("output"):"");
		if(this.args.get("input") == null || !input.isDirectory() || this.args.get("output") == null || this.args.get("order") == null) {
			return false;
		}
		return true;
	}
	
	/* This function was not written by me.
	 * Written by user on stack overflow
	 * Link: http://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
	 */
	
	private static boolean isInteger(String str) {
		return str.matches("^-?\\d+$");
	}
}
