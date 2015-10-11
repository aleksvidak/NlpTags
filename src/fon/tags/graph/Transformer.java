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
		TreeMap<String, Integer> target = new TreeMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			if (count >= max)
				break;

			target.put(entry.getKey(), entry.getValue());
			count++;
		}
		return target;
	}
}
