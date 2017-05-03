package entity.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import entity.intrfc.Crawler;
import entity.intrfc.SearchEngine;

public class NewYorkTimesCrawler implements Crawler {
	
	private SearchEngine searchEngine;
	
	private final String COOKIES = System.getProperty("cookies");
	
	public NewYorkTimesCrawler(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	@Override
	public Map<String, String> searchArticles(String query, int amount) {
		List<String> urls = this.searchEngine.search(query, 10);
		
		return fetchArticles(urls);
	}
	
	private Map<String, String> fetchArticles(List<String> urls) {
		return urls.stream()
					.map(this::fetchArticle)
					.filter(article -> !article.getValue().equals(""))
					.collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> a));
	}

	private Pair<String, String> fetchArticle(String url) {
		System.out.println("Fetching Article: " + url);
		
		Response res = null;
		try {
			res = Jsoup.connect(url).followRedirects(false).header("cookie", COOKIES).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Document doc = Jsoup.parse(res.body());
		String text = doc.getElementsByClass("story-body-text").text();
		
		return Pair.of(url, text); 
	}
}
