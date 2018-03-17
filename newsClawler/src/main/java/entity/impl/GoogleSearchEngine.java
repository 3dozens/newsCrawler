package entity.impl;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
	
	// for debug
	public List<String> readURLfromFile() {
		List<String> urls = new ArrayList<>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get("/home/kento/develop/data/new_documents/Sports/indoor_sports/ice_hockey/searched_urls"), StandardCharsets.UTF_8)) {
			String line; int i = 1;
			while ((line = br.readLine()) != null) {
				urls.add(line); //if (i++ == 100) break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		};
		
		return urls;
	}
	
	/**
	 * queryで検索し、検索結果のURLを返します
	 * @param query 検索クエリ
	 * @param amount 検索件数
	 * @return URL
	 */
	public List<String> search(String query, int amount) {
		// 外部インターフェースになるレイヤ
		// 必要件数が取れるまで年月を回して検索するメソッド
		
		List<String> urlList = new ArrayList<>();
		
		int year = ZonedDateTime.now().getYear();
		int noResultCount = 0;
		outer: while (true) {
			int month = 1;
			
			while (month <= 12) {
				if (year == ZonedDateTime.now().getYear() && month > ZonedDateTime.now().getMonthValue())
					break;
					
				List<String> result = search(query, year, month);
				urlList.addAll(result);
				System.out.println("total count so far: " + urlList.size());
				
				if (result.size() == 0) {
					if (noResultCount++ == 5)
						break outer; // 結果件数が連続で0件だった場合、中止する
				} else {
					noResultCount = 0;
				}
				
				try {
					Files.write(Paths.get("/home/kento/develop/data/new_documents/Business/lv3/technology/bio/searched_url.txt"), result, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (urlList.size() >= amount)
					break outer;
				
				month++;
			}
			year--; 
		}
		
		return urlList;
	}

	private List<String> search(String query, int year, int month) {
		// ある年月について、検索可能件数限界まで検索するレイヤ(100件くらい取れてきます)
		
		int startIndex = 1;
		List<String> urlList = new ArrayList<>();
		while (true) {
			// 1ループ = 1ページ分のリクエスト
			GoogleSearchResult result = search(query, year, month, startIndex);
			if (result == null || result.getItems() == null) {
				break; // result == null 100件検索リミットに到達した
						// result.getItems() == null 検索結果が0件だった
			}
			if (result.getQueries().getNextPage() == null) {
				// result.getQueries().getNextPage() == null 検索結果が次のページが存在しない
				// この場合、このquery year monthに対する最後の検索ページになるので、検索結果が存在する
				urlList.addAll(result.getItems().stream().map(Item::getLink).collect(toList()));
				break;
			}
			
			urlList.addAll(result.getItems().stream().map(Item::getLink).collect(toList()));
			startIndex = result.getQueries().getNextPage().getStartIndex();
		}
		
		System.out.println("result # for " + year + "/" + month + ": " + urlList.size());
		
		return urlList;
	}
	
	/**
	 * Google Custom Search API を用いて検索します
	 * 
	 * @param query 検索クエリ
	 * @param year 何年のデータを取得するか
	 * @param month 何月のデータを取得するか
	 * @param startIndex 検索開始位置
	 * @return 検索結果
	 */
	private GoogleSearchResult search(String query, int year, int month, int startIndex) {
		// 実際に検索するレイヤ
		// Google APIに1リクエストだけ発射するメソッド
		// TODO: New York Timesとの結合をなくす
		
		// URLの組み立て
		URL url = null; 
		String site = "www.nytimes.com/" + year + "/" + String.format("%02d", month) + "/*/";
		try {
			url = new URL("https://www.googleapis.com/customsearch/v1?"
					+ "key="         + this.API_KEY
					+ "&cx="         + this.CUSTOM_SEARCH_ENGINE_ID
					+ "&start="      + startIndex
					+ "&filter="     + "1" // 重複した結果を排除する
					+ "&siteSearch=" + URLEncoder.encode(site,  "UTF-8")
					+ "&q="          + URLEncoder.encode(query, "UTF-8"));
		} catch (MalformedURLException e) { // TODO: 例外をどこで処理するのがベストか?
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println("Request to Google API: " + url);
		
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
