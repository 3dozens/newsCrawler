package newsClawler.newsClawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import entity.impl.DocumentParser;
import entity.impl.GoogleSearchEngine;
import entity.impl.NewYorkTimesCrawler;
import entity.impl.OutputUtil;
import entity.intrfc.Crawler;
import entity.intrfc.SearchEngine;

public class Main {

	private static SearchEngine searchEngine;
	private static Crawler crawler;
	private static DocumentParser parser;
	
	static {
		// Dependency Injection
		searchEngine = new GoogleSearchEngine();
		crawler = new NewYorkTimesCrawler();
		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize, ssplit, pos");
		parser = new DocumentParser(new StanfordCoreNLP(properties));
	}
	
	public static void main(String[] args) throws IOException {
		String searchQ = "business technology bio";
		int amount = 1000;
		String outputPath = "/home/kento/develop/data/new_documents/Business/lv3/technology/bio/bio";
		
		Files.createDirectories(Paths.get(outputPath).getParent());
		
		List<String> urls = searchEngine.search(searchQ, amount);
		
		//List<String> urls = searchEngine.readURLfromFile();
		
		Map<String, String> articles = crawler.fetchArticles(urls); // URL => Text
		
		Map<String, List<String>> parsed = parse(articles); // Text => [parsed Text]
		
		OutputUtil.output(parsed, Paths.get(outputPath));
		
		System.out.println("process completed");
	}
	
	private static Map<String, List<String>> parse(Map<String, String> articles) {
		System.out.println("parsing...");
		
		return articles.entrySet().stream()
							.map(e -> Pair.of(e.getKey(), parser.parse(e.getValue()))) // valueをパース
							.collect(Collectors.toMap(Pair::getKey, Pair::getValue));  // またマップに詰め直す
	}
	
}
