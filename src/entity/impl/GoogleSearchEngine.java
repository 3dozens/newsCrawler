package entity.impl;

import java.util.List;

public class GoogleSearchEngine {
	
	private final String API_KEY;
	
	GoogleSearchEngine() {
		API_KEY = System.getenv("API_KEY");
	}
	
	public List<String> search() {
		return null;
	}
}
