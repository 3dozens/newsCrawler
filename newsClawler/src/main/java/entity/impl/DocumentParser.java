package entity.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;



public class DocumentParser {
	
	private StanfordCoreNLP coreNlp;
	
	private final List<String> ACCEPT_TAGS = Arrays.asList(
			"FW",										  // 英語以外の語
			"JJ", "JJR", "JJS",						  // 形容詞
			"NN", "NNS", "NNP", "NNPS",				  // 名詞
			"RB", "RBR", "RBS", "RP",				  // 副詞
			"VB", "VBD", "VBG", "VBN", "VBP", "VBZ" // 動詞
			);
	
	public DocumentParser(StanfordCoreNLP coreNlp) {
		this.coreNlp = coreNlp;
	}
	
	/**
	 * 入力された文章を形態素解析し、特定の品詞の単語のリストを返します。
	 * @param doc パースする文章
	 * @return 単語のリスト
	 */
	public List<String> parse(String doc) {
		Annotation annotation = new Annotation(doc);
		coreNlp.annotate(annotation);
		List<CoreLabel> labels = annotation.get(TokensAnnotation.class);
		
		// ACCEPT_TAGSの品詞のみを抜き出す
		return labels.stream()
						.filter(l -> this.ACCEPT_TAGS.contains(l.get(PartOfSpeechAnnotation.class)))
						.map(l -> l.get(TextAnnotation.class))
						.collect(Collectors.toList());
	}
	
	/**
	 * 入力された文章を形態素解析し、特定の品詞の単語のリストを返します。
	 * 一度に複数の文章をリストの形で受け付け、ネストしたリストの形で返却します。
	 * @param docs パースする文章
	 * @return [[文章1の単語1, 文章1の単語2...], [文章2の単語1...]...]
	 */
	public List<List<String>> parse(List<String> docs) {
		return docs.stream()
					.map(this::parse)
					.collect(Collectors.toList());
	}
	
}
