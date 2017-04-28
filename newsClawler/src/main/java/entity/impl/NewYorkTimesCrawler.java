package entity.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
	public List<String> searchArticles(String query) {
		List<String> urls = this.searchEngine.search(query);
		
		return fetchArticles(urls);
	}

	private List<String> fetchArticles(List<String> urls) {
		return urls.stream()
					.map(this::fetchArticle)
					.filter(article -> !article.equals(""))
					.collect(Collectors.toList());
	}

	private String fetchArticle(String url) {
System.out.println(url);
		
		Response res = null;
		try {
			res = Jsoup.connect(url).followRedirects(false).header("cookie", COOKIES).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO: URL => 文章 の形式にする
		Document doc = Jsoup.parse(res.body());
		return doc.getElementsByClass("story-body-text").text();
	}
}
