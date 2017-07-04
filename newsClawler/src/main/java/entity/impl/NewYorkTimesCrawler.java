package entity.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import entity.intrfc.Crawler;

public class NewYorkTimesCrawler implements Crawler {
	
	private final String COOKIES = System.getProperty("cookies"); // ブラウザからNewYorkTimesにアクセスして、クッキー "NYT-S" "NYT-BCET" を取得して "cookies" に指定してください。
																		  // クローラで取得したクッキーはなぜか使えない(クローラ対策？)
	
	@Override
	public Map<String, String> fetchArticles(List<String> urls) {
		
		Map<String, String> fetched = urls.stream().parallel()
					.map(this::fetchArticle)
					.filter(article -> StringUtils.isNotEmpty(article.getValue()))
					.collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> a));
		
		System.out.println(fetched.size() + " articles fetched.");
		
		return fetched;
	}

	private Pair<String, String> fetchArticle(String url) {
		System.out.println("Fetching Article: " + url);
		
		Response res = null;
		String text = null;
		try {
			res = Jsoup.connect(url).followRedirects(false).header("cookie", COOKIES).execute();
			text = Jsoup.parse(res.body()).getElementsByClass("story-body-text").text(); // TODO: 古い記事も story-body-text でいいのか？ → ダメ http://www.nytimes.com/1991/10/21/sports/football-costly-dolphin-fumble-saves-the-oilers.html
			if (StringUtils.isEmpty(text)) {
				text = Jsoup.parse(res.body()).getElementsByClass("p[itemprop=\"articleBody\"]").text(); // TODO: ここテスト 1995から取れてない
			}
		} catch (Exception e) {
			System.out.println("an Error occured while Fetching");
			text = "";
		}
		
		return Pair.of(url, text);
	}
}
