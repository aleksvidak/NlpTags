package fon.tags.nlp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

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
import fon.tags.graph.LemmasGraph;
import fon.tags.graph.Transformer;
import fon.tags.input.CustomStopwords;

public class Lemmatizer {
	protected StanfordCoreNLP pipeline;

	public Lemmatizer() {
		// Create StanfordCoreNLP object properties, with POS tagging
		// (required for lemmatization), and lemmatization
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, stopword");
		props.setProperty("customAnnotatorClass.stopword",
				"fon.tags.nlp.StopwordAnnotator");
		// StanfordCoreNLP loads a lot of models, so you probably
		// only want to do this once per execution
		this.pipeline = new StanfordCoreNLP(props);
	}

	// Destructure text into lemmas, returns lemmas list
	public TreeMap<String, Integer> lemmatize(String documentText, int noOfEntries) {
		
		List<String> lemmas = new LinkedList<String>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		this.pipeline.annotate(document);

		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of
				// lemmas
				lemmas.add(token.get(LemmaAnnotation.class).toLowerCase());
			}
		}

		HashMap<String, Integer> tags = new HashMap<String, Integer>();
		TreeMap<String, Integer> sortedTags = new TreeMap<String, Integer>();

		// remove top 5% and bottom 5% words by frequency
		tags = Transformer.makeDictionary(lemmas);
		sortedTags = Transformer.SortByValue(tags);

		int perc = 5 * sortedTags.size() / 100;

		for (int i = 0; i < perc; i++) {
			@SuppressWarnings("rawtypes")
			Iterator it = lemmas.iterator();

			Map.Entry<String, Integer> a = sortedTags.pollFirstEntry();
			while (it.hasNext()) {
				Object o = it.next();
				if (a.getKey().equalsIgnoreCase((String) o)) {
					it.remove();
				}
			}

			Map.Entry<String, Integer> b = sortedTags.pollLastEntry();
			while (it.hasNext()) {
				Object o = it.next();
				if (b.getKey().equalsIgnoreCase((String) o)) {
					it.remove();
				}
			}
		}

		tags = LemmasGraph.CreateGraph(lemmas);

		if (noOfEntries==0) 
			return Transformer.SortByValue(tags);		
		else 
			return Transformer.returnFirstEntries(noOfEntries, Transformer.SortByValue(tags));
	}

	// lemmatize given text and return lemmas without stopwords
	public TreeMap<String, Integer> lemmatizeNoStopWords(String documentText, int noOfEntries) {
		
		List<String> lemmasNoStopWords = new LinkedList<String>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		// run all Annotators on this text
		this.pipeline.annotate(document);
		// instantiate nlp tagger to get out noun words only
		MaxentTagger tagger = new MaxentTagger(
				"edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

		CustomStopwords csw = new CustomStopwords();

		String tag = "";
		
		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		Set<?> stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of
				// lemmas
				TaggedWord tgw = new TaggedWord();

				String word = token.get(LemmaAnnotation.class).toLowerCase();
				tag = tagger.tagString(word);
				tgw.setFromString(tag, "_");

				if (!tgw.tag().matches("NN |NNS |NNP |NNPS ")
						|| word.length() < 3 || word.matches("-lrb-|-rrb-|-lsb-|-rsb-") || stopWords.contains(word)
						|| csw.is(word)) {

				} else {
					lemmasNoStopWords.add(token.get(LemmaAnnotation.class)
							.toLowerCase());
				}

			}
		}
		// System.out.println(lemmasNoStopWords);

		HashMap<String, Integer> tags = new HashMap<String, Integer>();


		tags = LemmasGraph.CreateGraph(lemmasNoStopWords);

		if (noOfEntries==0)
			return Transformer.SortByValue(tags);
		else	
			return Transformer.returnFirstEntries(noOfEntries, Transformer.SortByValue(tags));
	}

}
