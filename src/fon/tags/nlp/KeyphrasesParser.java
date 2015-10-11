package fon.tags.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import fon.tags.graph.NounPhrases;
import fon.tags.graph.PhrasesGraph;
import fon.tags.graph.Transformer;

public class KeyphrasesParser {
	protected StanfordCoreNLP pipeline;

	public KeyphrasesParser() {
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, stopword");
		props.setProperty("customAnnotatorClass.stopword",
				"fon.tags.nlp.StopwordAnnotator");
		// StanfordCoreNLP loads a lot of models, so you probably
		// only want to do this once per execution
		this.pipeline = new StanfordCoreNLP(props);
	}

	public TreeMap<String, Integer> toKeyphrases(String documentText,
			int noOfEntries) throws IOException {

		List<CoreLabel> tokensList = new LinkedList<CoreLabel>();

		// for window
		List<String> allWords = new LinkedList<String>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		// run all Annotators on this text
		this.pipeline.annotate(document);

		LexicalizedParser lp = LexicalizedParser
				.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

		Tree parse = null;
		List<String> nounPhrasesLocal = new ArrayList<String>();
		List<String> nounPhrases = new ArrayList<String>();

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		// number of words in a single sentence
		int wordsCount = 0;
		// number of words in all sentences
		int wordsCountAll = 0;
		// median sentence length
		int median = 0;
		// number of sentences
		int noSentences = 0;

		// Iterate over all of the sentences found
		for (CoreMap sentence : sentences) {

			// clear tokens in previously scanned sentence
			tokensList.clear();

			// adding 1 to number of sentences
			noSentences++;

			wordsCountAll += wordsCount;
			// reset counter
			wordsCount = 0;
			
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the token for each word into the list of
				// tokens
				wordsCount++;
				String tokenString = token.get(LemmaAnnotation.class)
						.toLowerCase();
				String word = token.word().toLowerCase();
				// remove brackets because they are displayed in a form that is
				// longer than 2 characters
				if (word.matches("-lrb-|-rrb-|-lsb-|-rsb-")
						|| word.length() < 3) {
					// do nothing
				} else {
					allWords.add(word);
				}

				if (tokenString.length() > 2
						&& !tokenString.matches("-lrb-|-rrb-|-lsb-|-rsb-")) {
					tokensList.add(token);
				}

			}

			parse = lp.apply(tokensList);

			// get noun phrases based on the annotations in the given tree
			nounPhrasesLocal = NounPhrases.GetNounPhrases(parse);

			for (String phrase : nounPhrasesLocal) {
				nounPhrases.add(phrase);
			}
		}

		// median number of words per sentence, for the window
		median = wordsCountAll / noSentences;

		TreeMap<String, Integer> score;

		score = PhrasesGraph.createGraph(SlidingWindow.CreateSlidingWindow(
				median, allWords, nounPhrases));
		// System.out.println(score);
		// if user set 0 to the desired keyphrases result get all keyphrases,
		// else get asked number
		if (noOfEntries == 0)
			return Transformer.sortByValue(score);
		else
			return Transformer.returnFirstEntries(noOfEntries,
					Transformer.sortByValue(score));

	}

}