package entity.impl;

import java.util.Arrays;
import java.util.List;

/**
 * Google Custom Search API が返すJSONと対応した構造のクラス
 * @author Kento Hayasaka thunders.1028@gmail.com
 *
 */
public class GoogleSearchResult {
	private List<Item> items;
	private Queries queries;

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Queries getQueries() {
		return queries;
	}

	public void setQueries(Queries queries) {
		this.queries = queries;
	}

	static public class Item {
		private String link;

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}
	}
	
	static public class Queries {
		private List<NextPage> nextPage; // Search APIから、要素がひとつの配列の形のJSONで来る

		public NextPage getNextPage() {
			try {
				return nextPage.get(0);
			} catch (Exception e) {
				System.out.println("No Next Page"); // TODO: 例外を潰していい?
				return null;
			}
		}

		public void setNextPage(NextPage nextPage) {
			this.nextPage = Arrays.asList(nextPage);
		}
	}
	static public class NextPage {
		private int startIndex;

		public int getStartIndex() {
			return startIndex;
		}

		public void setStartIndex(int startIndex) {
			this.startIndex = startIndex;
		}
	}
}
