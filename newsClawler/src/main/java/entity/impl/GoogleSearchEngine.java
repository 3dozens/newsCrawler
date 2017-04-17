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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import entity.impl.GoogleSearchResult.Item;
import entity.intrfc.SearchEngine;

public class GoogleSearchEngine implements SearchEngine {
	
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

		List<GoogleSearchResult> resultList = new ArrayList<>();
		int startIndex = 1;
		for (int i = 0; i < 10; i++) {
			// TODO: ページ数の調整
			// 1ループ = 1ページ分のリクエスト
			
			// URLの組み立て
			URL url = null; 
			try {
				url = new URL("https://www.googleapis.com/customsearch/v1?"
						+ "key=" + API_KEY
						+ "&cx=" + CUSTOM_SEARCH_ENGINE_ID
						+ "&start=" + startIndex
						+ "&q="  + URLEncoder.encode(query, "UTF-8"));
			} catch (MalformedURLException e) { // TODO: 例外をどこで処理するのがベストか?
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// レスポンスを受け取るReaderの組み立て
			Reader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}

			// APIからレスポンスを受ける
			// JSONからオブジェクトにマップ
			// ひとつのGoogleSearchResult = 1ページ分のレスポンス
			GoogleSearchResult result = new Gson().fromJson(reader, GoogleSearchResult.class);
			
			startIndex = result.getQueries().getNextPage().getStartIndex();
			resultList.add(result);
		}
		
		List<String> urls = resultList.stream()
										.flatMap(result -> result.getItems().stream())
										.map(Item::getLink)
										.collect(Collectors.toList());

		return urls;
	}
}
