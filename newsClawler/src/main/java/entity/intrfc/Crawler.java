package entity.intrfc;

import java.util.List;

public interface Crawler {

	/**
	 * queryで検索し、該当する記事すべての本文を取得する
	 * @param query 検索する語
	 * @return 記事の本文
	 */
	public List<String> searchArticles(String query);
	
}
