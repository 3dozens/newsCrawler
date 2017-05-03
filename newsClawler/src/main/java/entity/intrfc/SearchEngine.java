package entity.intrfc;

import java.util.List;

public interface SearchEngine {

	public List<String> search(String query, int amount);
	
}
