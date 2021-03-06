package fon.tags.graph;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fon.tags.nlp.ValueComparator;

public class Transformer {
	// make unique lemmas dictionary
	public static TreeMap<String, Integer> makeDictionary(List<String> lemmas) {

		TreeMap<String, Integer> dictionary = new TreeMap<String, Integer>();

		for (String lemma : lemmas) {

			if (dictionary.containsKey(lemma)) {
				dictionary.put(lemma, dictionary.get(lemma) + 1);
			} else {
				dictionary.put(lemma, 1);
			}
		}
		return dictionary;
	}
	
	// merge every scored keyword with the number of its appearances in the text

	public static TreeMap<String, Integer> mergeKeywords(TreeMap<String, Integer> keywords, TreeMap<String, Integer> dictionary) {
		TreeMap<String, Integer> mergedMap = new TreeMap<String, Integer>();
		
		for (Map.Entry<String, Integer> keyword : keywords.entrySet()) {
			if (dictionary.containsKey(keyword.getKey())) {
				mergedMap.put(keyword.getKey(), dictionary.get(keyword.getKey()));
			}
			else {
				// do nothing
			}
				
		}
		
		return mergedMap;
	}
	// sort map by values
	public static TreeMap<String, Integer> sortByValue(
			TreeMap<String, Integer> map) {

		ValueComparator vc = new ValueComparator(map);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
		sortedMap.putAll(map);

		return sortedMap;
	}

	// return given number of keywords/keyphrases
	public static TreeMap<String, Integer> returnFirstEntries(int max,
			TreeMap<String, Integer> source) {
		int count = 0;
		TreeMap<String, Integer> result = new TreeMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			if (count >= max)
				break;

			result.put(entry.getKey(), entry.getValue());
			count++;
		}
		return sortByValue(result);
	}
}
