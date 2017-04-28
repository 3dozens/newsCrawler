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
		this.API_KEY = System.getProperty("api_key"); // VMオプションの-Dなどを使って、事前にシステムプロパティを設定してください
		this.CUSTOM_SEARCH_ENGINE_ID = System.getProperty("custom_search_api");
	}
	
	/**
	 * queryで検索し、検索結果のURLを返します
	 * @param query 検索クエリ
	 * @return URL
	 */
	public List<String> search(String query) {
		List<GoogleSearchResult> resultList = new ArrayList<>();
		int startIndex = 1;
		for (int i = 0; i < 100; i++) {
			// TODO: ページ数の調整
			// 1ループ = 1ページ分のリクエスト
			GoogleSearchResult result = search(query, startIndex);
			if (result == null) break;
			
			resultList.add(result);
			startIndex = result.getQueries().getNextPage().getStartIndex();
		}
		
		// 検索結果からURLを取り出す
		return resultList.stream()
							.flatMap(result -> result.getItems().stream())
							.map(Item::getLink)
							.collect(Collectors.toList());
	}
	
	/**
	 * startIndexの位置から検索します
	 * @param query 検索クエリ
	 * @param startIndex 検索開始位置
	 * @return 検索結果URL
	 */
	private GoogleSearchResult search(String query, int startIndex) {
		// URLの組み立て
		URL url = null; 
		try { // TODO: dataRistrict 使ってみる？
			url = new URL("https://www.googleapis.com/customsearch/v1?"
					+ "key=" + this.API_KEY
					+ "&cx=" + this.CUSTOM_SEARCH_ENGINE_ID
					+ "&start=" + startIndex
					+ "&q="  + URLEncoder.encode(query, "UTF-8"));
		} catch (MalformedURLException e) { // TODO: 例外をどこで処理するのがベストか?
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
System.out.println(url);
		// レスポンスを受け取るReaderの組み立て
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
		} catch (IOException e) {
			System.out.println("item limit reached");
			return null;
		}

		// APIからレスポンスを受ける
		// JSONからオブジェクトにマップ
		// ひとつのGoogleSearchResult = 1ページ分のレスポンス
		return new Gson().fromJson(reader, GoogleSearchResult.class);
	}
}
