package newsCrawler;

import java.util.List;

public interface Crawler {

	List<String> searchArticles(String query);
	
}
