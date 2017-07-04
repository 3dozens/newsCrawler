package entity.intrfc;

import java.util.List;
import java.util.Map;

public interface Crawler {

	public Map<String, String> fetchArticles(List<String> urls);
	
}
