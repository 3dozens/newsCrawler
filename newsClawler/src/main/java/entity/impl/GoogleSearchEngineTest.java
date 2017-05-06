package entity.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GoogleSearchEngine.class})
public class GoogleSearchEngineTest {

	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testSearchStringInt() throws Exception {
		// パラメータ設定
		String query = "sports";
		int amount = 12000; // 10年分
		List<String> searchResult = new ArrayList<>();
		for (int i = 0; i < 100; i++)
			searchResult.add("result" + i);
		
		
		// モック化
		GoogleSearchEngine searchEngine = PowerMockito.spy(new GoogleSearchEngine());
		for (int i = 2008; i <= 2017; i++) 
			for (int j = 1; j <= 12; j++) 
				PowerMockito.doReturn(searchResult).when(searchEngine, "search", query, i, j);
		
		// 実行
		List<String> retVal = searchEngine.search(query, amount);
		
		// アサーション
		assertThat(retVal.size(), is(amount));
		
		// 呼び出し確認
		for (int i = 2008; i <= 2017; i++) 
			for (int j = 1; j <= 12; j++) 
				PowerMockito.verifyPrivate(searchEngine).invoke("search", query, i, j);
		
		//PowerMockito.verifyNoMoreInteractions(searchEngine);
	}

}
