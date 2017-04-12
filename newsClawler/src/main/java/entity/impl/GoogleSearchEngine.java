package entity.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class GoogleSearchEngine {
	
	private final String API_KEY;
	private final String CUSTOM_SEARCH_ENGINE_ID;
	
	GoogleSearchEngine() {
		API_KEY = System.getenv("API_KEY");
		CUSTOM_SEARCH_ENGINE_ID = System.getenv("CUSTOM_SEARCH_ENGINE_ID");
	}
	
	/**
	 * queryで検索し、結果のURLを返します
	 * @param query 検索クエリ
	 * @return URL
	 */
	public List<String> search(String query) {

		URL url; 
		try {
			url = new URL("https://www.googleapis.com/customsearch/v1?"
					+ "key=" + API_KEY
					+ "&cx=" + CUSTOM_SEARCH_ENGINE_ID
					+ "&q=" + URLEncoder.encode(query, "UTF-8"));
		} catch (MalformedURLException e) { // TODO: 例外をどこで処理するのがベストか?
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		
		return null;
		
	}
}
