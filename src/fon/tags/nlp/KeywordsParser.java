package fon.tags.nlp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.StopAnalyzer;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import fon.tags.graph.Transformer;
import fon.tags.graph.KeywordsGraph;
import fon.tags.input.CustomStopwords;

public class KeywordsParser {
	protected StanfordCoreNLP pipeline;
	protected MaxentTagger tagger;

	public KeywordsParser() {
		// Create StanfordCoreNLP object properties, with POS tagging
		// (required for lemmatization), and lemmatization
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, stopword");
		props.setProperty("customAnnotatorClass.stopword",
				"fon.tags.nlp.StopwordAnnotator");
		// StanfordCoreNLP loads a lot of models, so you probably
		// only want to do this once per execution
		this.tagger = new MaxentTagger(
				"edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

		this.pipeline = new StanfordCoreNLP(props);
	}

	// Destructure text into lemmas, returns lemmas list without top and bottom
	// 5% words
	public TreeMap<String, Integer> toKeywords(String documentText,
			int noOfEntries) {

		List<String> lemmas = new LinkedList<String>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		this.pipeline.annotate(document);

		// get all senteces in a text
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		String tag = "";
		String lemma = "";
		TaggedWord tgw = new TaggedWord();
		// Iterate over all of the sentences found
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of
				// lemmas
				lemma = token.get(LemmaAnnotation.class).toLowerCase();
				tag = tagger.tagString(lemma);
				tgw.setFromString(tag, "_");

				if (!tgw.tag().matches("NN |NNS |NNP |NNPS ")
						|| lemma.length() < 3
						|| lemma.contains("_")
						|| lemma.matches("-lrb-|-rrb-|-lsb-|-rsb-|-rcb-|-lcb-")) {
					// do nothing
				} else
					lemmas.add(lemma);
			}
		}

		TreeMap<String, Integer> keywords = new TreeMap<String, Integer>();
		TreeMap<String, Integer> sortedKeywords = new TreeMap<String, Integer>();

		// remove top 5% and bottom 5% words by frequency
		keywords = Transformer.makeDictionary(lemmas);
		sortedKeywords = Transformer.sortByValue(keywords);

		int fivePercent = 5 * sortedKeywords.size() / 100;

		@SuppressWarnings("rawtypes")
		Iterator it = lemmas.iterator();

		for (int i = 0; i < fivePercent; i++) {

			Map.Entry<String, Integer> a = sortedKeywords.pollFirstEntry();
			while (it.hasNext()) {
				Object o = it.next();
				if (a.getKey().equalsIgnoreCase((String) o)) {
					it.remove();
				}
			}

			Map.Entry<String, Integer> b = sortedKeywords.pollLastEntry();
			while (it.hasNext()) {
				Object o = it.next();
				if (b.getKey().equalsIgnoreCase((String) o)) {
					it.remove();
				}
			}
		}

		keywords = KeywordsGraph.createGraph(lemmas);

		if (noOfEntries == 0)
			return Transformer.sortByValue(keywords);
		else
			return Transformer.returnFirstEntries(noOfEntries,
					Transformer.sortByValue(keywords));
	}

	// lemmatize given text and return lemmas without stopwords
	public TreeMap<String, Integer> toKeywordsNoStop(String documentText,
			int noOfEntries) {

		List<String> lemmas = new LinkedList<String>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		// run all Annotators on this text
		this.pipeline.annotate(document);
		// instantiate nlp tagger to get out noun words only
		final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
		final String HTML_REGEX = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";
		final String EMAIL_REGEX = 
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		
		Pattern purl = Pattern.compile(URL_REGEX);
		Pattern phtml = Pattern.compile(HTML_REGEX);
		Pattern pemail = Pattern.compile(EMAIL_REGEX);
		Pattern any = Pattern.compile("[^a-zA-Z\\s\\-]");
        
		CustomStopwords csw = new CustomStopwords();
		
		String tag = "";
		String lemma = "";
		TaggedWord tgw = new TaggedWord();

		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		Set<?> stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of
				// lemmas
				
				lemma = token.get(LemmaAnnotation.class).toLowerCase();
				Matcher matcherUrl = purl.matcher(lemma);
				Matcher matcherHtml = phtml.matcher(lemma);
				Matcher matcherEmail = pemail.matcher(lemma);
				Matcher matcherAny = any.matcher(lemma);
			
				boolean isURL=matcherUrl.matches();
				boolean isHTML=matcherHtml.matches();
				boolean isEMAIL=matcherEmail.matches();
				boolean isSpecial = matcherAny.find();
				
				tag = tagger.tagString(lemma);
				tgw.setFromString(tag, "_");
				//check if a lemma matches url regex
				

				// if tagged word is not noun, or it is less than 3 characters
				// long or it is in the list of frequent english words or it is a url do
				// nothing
				if (!tgw.tag().matches("NN |NNS |NNP |NNPS ")
						|| lemma.length() < 3
						|| lemma.matches("-lrb-|-rrb-|-lsb-|-rsb-|-rcb-|-lcb-")
						|| stopWords.contains(lemma) || csw.is(lemma) || isURL || isHTML || isEMAIL || isSpecial) {
				} else {
					lemmas.add(lemma);
				}

			}
		}

		TreeMap<String, Integer> keywords = new TreeMap<String, Integer>();
		TreeMap<String, Integer> dictionary = new TreeMap<String, Integer>();
		TreeMap<String, Integer> keywords2 = new TreeMap<String, Integer>();
		// counting the number of appearances of lemmas, need to make a new variable to put the data
		dictionary = Transformer.makeDictionary(lemmas);
		// return keywords
		keywords = KeywordsGraph.createGraph(lemmas);

		keywords2 = Transformer.mergeKeywords(keywords, dictionary);
		System.out.println("key "+keywords);
		System.out.println("dic "+dictionary);
		System.out.println("res "+keywords2);
		
		
		if (noOfEntries == 0)
			return Transformer.sortByValue(keywords);
		else
			return Transformer.returnFirstEntries(noOfEntries,
					Transformer.sortByValue(keywords));
	}

}
