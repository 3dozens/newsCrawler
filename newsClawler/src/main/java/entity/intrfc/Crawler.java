package entity.intrfc;

import java.util.Map;

public interface Crawler {

	/**
	 * queryで検索し、該当する記事すべての本文を取得する
	 * @param query 検索する語
	 * @param amount 取得する記事数
	 * @return 記事の本文
	 */
	public Map<String, String> searchArticles(String query, int amount);
	
}
