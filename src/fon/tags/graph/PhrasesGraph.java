package fon.tags.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

public class PhrasesGraph {

	public static HashMap<String, Integer> CreateGraph(
			HashMap<String, Integer> dictionary) {

		Graph<MyNode, MyLink> phrasesGraph = new UndirectedSparseMultigraph<MyNode, MyLink>();

		Iterator<String> iterator = dictionary.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			
			int weight = dictionary.get(key);

			MyNode previous = new MyNode(key.split(",")[0]);
			MyNode current = new MyNode(key.split(",")[1]);

			MyLink noviLink = new MyLink(weight, key.split(",")[0] + "-"
					+ key.split(",")[1]);
			phrasesGraph.addEdge(noviLink, previous, current);
		}

		Collection<MyNode> nodes = new LinkedList<MyNode>();
		nodes = phrasesGraph.getVertices();
		DegreeScorer<MyNode> graphscorer = new DegreeScorer<MyNode>(
				phrasesGraph);
		int score = 0;
		HashMap<String, Integer> lemmasScore = new HashMap<String, Integer>();
		for (MyNode node : nodes) {

			score = graphscorer.getVertexScore(node);
			lemmasScore.put(node.word, score);
		}

		return lemmasScore;
	}

}
