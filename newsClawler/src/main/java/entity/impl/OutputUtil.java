package entity.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OutputUtil {
	
	private OutputUtil() {}
	
	/**
	 * パースした文章をファイルに出力します
	 * 一つの文章ごとに、ファイルを連番で出力します
	 * @param parsed パースした文章
	 * @param filePath ファイルパス(このパスのファイル名の後ろに連番がついたファイルが出力される) 
	 *                    例: 入力ファイル名:sports -> 実際のファイル名:sports_1, sports_2...
	 */
	public static void output(List<List<String>> parsed, Path filePath) {
		OutputUtil.output(parsed, filePath, OutputUtil.findMaxIndex(filePath) + 1);
	}

	/**
	 * パースした文章をファイルに出力します
	 * 一つの文章ごとに、ファイルをmaxからの連番で出力します
	 * @param parsed パースした文章
	 * @param filePath 出力するファイルパス
	 * @param startIndex 連番のスタート
	 */
	private static void output(List<List<String>> parsed, Path filePath, int startIndex) {
		int i = startIndex;
		for (List<String> words : parsed) {
			Path writePath = Paths.get(filePath.toString() + "_" + i++);
			try {
				Files.write(writePath, words, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
			} catch (IOException e) {
				e.printStackTrace(); // TODO: 例外処理の方法を考える
			}
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
