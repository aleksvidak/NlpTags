package fon.tags.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

public class PhrasesGraph {

	// create graph based on the given HashMap
	public static TreeMap<String, Integer> createGraph(
			TreeMap<String, Integer> dictionary) {

		Graph<CustomNode, CustomLink> phrasesGraph = new UndirectedSparseMultigraph<CustomNode, CustomLink>();

		Iterator<String> iterator = dictionary.keySet().iterator();
		// create nodes from phrases
		while (iterator.hasNext()) {
			String key = iterator.next();

			int weight = dictionary.get(key);

			CustomNode previous = new CustomNode(key.split(",")[0]);
			CustomNode current = new CustomNode(key.split(",")[1]);

			CustomLink newLink = new CustomLink(weight, key.split(",")[0] + "-"
					+ key.split(",")[1]);
			phrasesGraph.addEdge(newLink, previous, current);
		}

		Collection<CustomNode> nodes = new LinkedList<CustomNode>();

		nodes = phrasesGraph.getVertices();

		DegreeScorer<CustomNode> graphscorer = new DegreeScorer<CustomNode>(
				phrasesGraph);
		int score = 0;
		TreeMap<String, Integer> phrasesScore = new TreeMap<String, Integer>();
		for (CustomNode node : nodes) {

			score = graphscorer.getVertexScore(node);
			phrasesScore.put(node.word, score);
		}
		// return scored keyphrases
		return phrasesScore;
	}

}
