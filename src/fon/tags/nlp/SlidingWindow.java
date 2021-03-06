package fon.tags.nlp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SlidingWindow {

	public static TreeMap<String, Integer> CreateSlidingWindow(int windowSize,
			List<String> allWords, List<String> nounPhrases) {

		// instantiate a Deque (Queue) which will serve for the sliding window
		Deque<String> Q = new ArrayDeque<String>();

		// instantiate a map where pair of phrases are put with weight between
		// them
		Map<String, Integer> pairsOfPhrases = new TreeMap<String, Integer>();

		// create initial window with size of windowSize
		for (int i = 0; i < windowSize; i++) {
			Q.add(allWords.get(i));
		}

		int jump = 0;
		int pairOfPhrasesValue = 0;

		int i = windowSize;

		// move through the list of noun phrases in two "for" loops
		for (int j = 0; j < nounPhrases.size(); j++) {
			if (!Q.toString().replace(",", "").contains(nounPhrases.get(j))) {

			} else {
				//position to the found noun phrase 
				jump = windowSize
						- Q.toString()
								.replace(",", "")
								.substring(
										Q.toString().replace(",", "")
												.indexOf(nounPhrases.get(j)))
								.replace("[", "").replace("]", "")
								.split("\\s+").length;
				while (jump > 0 && i < allWords.size()) {

					// remove first element in the deque
					Q.pollFirst();
					
					// add element from allWords at the end of the deque
					Q.offerLast(allWords.get(i));
					
					i++;
					jump--;
				}

				for (int k = j + 1; k < nounPhrases.size(); k++) {
					if (Q.toString().replace(",", "")
							.contains(nounPhrases.get(k))) {

						// if pair of phrases exists increment edge value
						if (pairsOfPhrases.containsKey(nounPhrases.get(j) + ","
								+ nounPhrases.get(k))) {
							pairOfPhrasesValue = pairsOfPhrases.get(nounPhrases
									.get(j) + "," + nounPhrases.get(k));

							pairsOfPhrases.put(nounPhrases.get(j) + ","
									+ nounPhrases.get(k), ++pairOfPhrasesValue);
						} else {

							// if pair of phrases doesn't exist add new map element
							pairsOfPhrases.put(nounPhrases.get(j) + ","
									+ nounPhrases.get(k), 1);
						}

					}
				}
			}

		}

		return (TreeMap<String, Integer>) pairsOfPhrases;

	}
}
