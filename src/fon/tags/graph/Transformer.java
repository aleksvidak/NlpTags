package fon.tags.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import fon.tags.nlp.ValueComparator;

public class Transformer {
	//make dictionary from lemmas list
		public static HashMap<String, Integer> makeDictionary(List<String> lemmas) {
			
			HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

			for (String lemma : lemmas) {

				if (dictionary.containsKey(lemma)) {				
					dictionary.put(lemma, dictionary.get(lemma) + 1);
				}
				else {
					dictionary.put(lemma, 1);
				}
			}
			return dictionary;
		}
		
		//sort map by values 		
		public static TreeMap<String, Integer> SortByValue(
				HashMap<String, Integer> map) {
			
			ValueComparator vc = new ValueComparator(map);
			TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
			sortedMap.putAll(map);
			
			return sortedMap;
		}
		
		public static TreeMap<String,Integer> returnFirstEntries(int max, TreeMap<String,Integer> source) {
			  int count = 0;
			  TreeMap<String,Integer> target = new TreeMap<String,Integer>();
			  for (Map.Entry<String,Integer> entry:source.entrySet()) {
			     if (count >= max) break;

			     target.put(entry.getKey(), entry.getValue());
			     count++;
			  }
			  return target;
			}
}
