package entity.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OutputUtil {
	
	private OutputUtil() {}
	
	/**
	 * パースした文章をファイルに出力します
	 * 一つの文章ごとに、ファイルを連番で出力します
	 * @param parsed パースした文章のマップ
	 * @param filePath ファイルパス(このパスのファイル名の後ろに連番がついたファイルが出力される) 
	 *                    例: 入力ファイル名:sports -> 実際のファイル名:sports_1, sports_2...
	 */
	public static void output(Map<String, List<String>> parsed, Path filePath) {
		try {
			Files.createDirectories(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		OutputUtil.output(parsed, filePath, OutputUtil.findMaxIndex(filePath) + 1);
	}

	/**
	 * パースした文章をファイルに出力します
	 * 一つの文章ごとに、ファイルをmaxからの連番で出力します
	 * @param parsed パースした文章
	 * @param filePath 出力するファイルパス
	 * @param startIndex 連番のスタート
	 */
	private static void output(Map<String, List<String>> parsed, Path filePath, int startIndex) {
		Path urlPath = Paths.get(filePath.toString() + "_urlinfo.txt");
		
		int i = startIndex;
		for (Map.Entry<String, List<String>> entry : parsed.entrySet()) {
			Path writePath = Paths.get(filePath.toString() + "_" + i++);
			
			outputParsedText(writePath, entry.getValue());
			
			outputURLInfo(urlPath, entry.getKey(), writePath.getFileName().toString());
		}
	}

	/**
	 * パースした文章をPathに出力します
	 * @param filePath 出力先Path
	 * @param parsed パースした文章
	 */
	private static void outputParsedText(Path filePath, List<String> parsed) {
		try {
			Files.write(filePath, parsed, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace(); // TODO: 例外処理の方法を考える
		}
	}
	
	/**
	 * 取得した記事のURL情報をPathに出力します
	 * @param filePath 出力先Path
	 * @param url 記事のURL
	 * @param fileName 記事を出力したファイル名
	 */
	private static void outputURLInfo(Path filePath, String url, String fileName) {
		try {
			Files.write(filePath, (fileName + ":" + url + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * すでに存在するファイルの連番の最大値を取得します
	 * @param filePath このパスのファイルと同名のファイルの連番を処理します
	 * @return 最大値
	 */
	private static int findMaxIndex(Path filePath) {
		Path dirPath = filePath.getParent();
		Pattern p = Pattern.compile(filePath.getFileName() + "_(\\d+)$");
		
		int max = 0;
		try (Stream<Path> stream = Files.list(dirPath)){
			max = stream
						.map(path -> path.getFileName().toString())
						.map(p::matcher)
						.filter(Matcher::matches)
						.map(m -> m.group(1))
						.mapToInt(Integer::parseInt)
						.max()
						.orElse(0);
		} catch (IOException e1) {
			e1.printStackTrace(); // TODO: 例外処理の方法を考える
		}
		
		return max;
	}
	
}
