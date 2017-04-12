package entity.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

public class GoogleSearchEngine {
	
	private final String API_KEY;
	private final String CUSTOM_SEARCH_ENGINE_ID;
	
	public GoogleSearchEngine() {
		API_KEY = System.getProperty("api_key"); // VMオプションの-Dなどを使って、事前にシステムプロパティを設定してください
		CUSTOM_SEARCH_ENGINE_ID = System.getProperty("custom_search_api");
	}
	
	/**
	 * queryで検索し、検索結果のURLを返します
	 * @param query 検索クエリ
	 * @return URL
	 */
	public List<String> search(String query) {

		URL url = null; 
		try {
			url = new URL("https://www.googleapis.com/customsearch/v1?"
					+ "key=" + API_KEY
					+ "&cx=" + CUSTOM_SEARCH_ENGINE_ID
					+ "&q="  + URLEncoder.encode(query, "UTF-8"));
		} catch (MalformedURLException e) { // TODO: 例外をどこで処理するのがベストか?
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		GoogleSearchResult result = new Gson().fromJson(reader, GoogleSearchResult.class);
		
		List<String> urls = result.getItems().stream() // TODO: 検索ページ数を増やす
												.map(Item::getLink)
												.collect(Collectors.toList());
		
		return urls;
	}
}
