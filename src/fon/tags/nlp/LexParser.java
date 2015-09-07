package fon.tags.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class LexParser {
	protected StanfordCoreNLP pipeline;

	public LexParser() {
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, stopword");
		props.setProperty("customAnnotatorClass.stopword", "fon.tags.nlp.StopwordAnnotator");
		// StanfordCoreNLP loads a lot of models, so you probably
		// only want to do this once per execution
		this.pipeline = new StanfordCoreNLP(props);
	}

	public TreeMap<String, Integer> lemmatizePhrases(String documentText, int noOfEntries) throws IOException {
		List<CoreLabel> lemmasPhrases = new LinkedList<CoreLabel>();
		
		//for window
		List<String> allWords = new LinkedList<String>();
		
		//HashMap<String, Integer> hashLemmas = new HashMap<String, Integer>();
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		// run all Annotators on this text
		this.pipeline.annotate(document);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>();
		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		Tree parse = null;
		List<String> nounPhrasesLocal = new ArrayList<String>();
		List<String> nounPhrases = new ArrayList<String>();


		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		//number of words in a single sentence
		int sentenceSize = 0;
		//number of words in all sentences
		int sentenceSizeAll = 0;
		//median sentence length
		int median = 0;
		//number of sentences
		int noSentences = 0;
		
		// Iterate over all of the sentences found
		for (CoreMap sentence : sentences) {
			
			//clear phrases in previously scanned sentence
			lemmasPhrases.clear();
			
			//adding 1 to number of sentences
			noSentences++;
			
			sentenceSizeAll+=sentenceSize;
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the token for each word into the list of
				// tokens
				
				sentenceSize++;
				String tokenString = token.get(LemmaAnnotation.class).toLowerCase();
				String word = token.word().toLowerCase();

					if (word.matches("-lrb-|-rrb-|-lsb-|-rsb-") || word.length()<2) {

						}
						else {
							allWords.add(word);
						}
					
					if (tokenString.length()>2) {
						lemmasPhrases.add(token);
					}
				
			}
			
			parse = lp.apply(lemmasPhrases);
			nounPhrasesLocal = NounPhrases.GetNounPhrases(parse);

			for (String phrase : nounPhrasesLocal) {
				nounPhrases.add(phrase);
			}
		}

		//get median length of sentences
		median=sentenceSizeAll/noSentences;
		
		HashMap<String, Integer> scorre;
		scorre = PhrasesGraph.CreateGraph(SlidingWindow.CreateSlidingWindow(median, allWords, nounPhrases));
		//System.out.println(scorre);
		if (noOfEntries==0) 
			return Transformer.SortByValue(scorre);
		else 
			return Transformer.returnFirstEntries(noOfEntries, Transformer.SortByValue(scorre));

	}




}