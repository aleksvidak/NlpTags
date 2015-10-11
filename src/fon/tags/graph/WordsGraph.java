package fon.tags.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

public class WordsGraph {

	// Lemmas become nodes connected with weighted edges (number of immediate
	// bigrams in text)

	public static TreeMap<String, Integer> createGraph(List<String> dictionary) {
		Graph<CustomNode, CustomLink> lemmasGraph = new UndirectedSparseMultigraph<CustomNode, CustomLink>();

		String previous = "";
		// create graph
		for (String current : dictionary) {
			int c = 1;

			CustomNode currentNode = new CustomNode(current);
			CustomNode previousNode = new CustomNode(previous);

			if (!lemmasGraph.containsVertex(currentNode)) {
				lemmasGraph.addVertex(currentNode);
			}

			if (previous != ""
					&& lemmasGraph.findEdge(previousNode, currentNode) == null) {
				CustomLink noviLink = new CustomLink(c, previousNode + "-"
						+ currentNode);
				lemmasGraph.addEdge(noviLink, previousNode, currentNode);
			}

			else if (previous != ""
					&& lemmasGraph.findEdge(previousNode, currentNode) != null
					&& lemmasGraph.containsEdge(lemmasGraph.findEdge(
							previousNode, currentNode))) {
				CustomLink link = lemmasGraph.findEdge(previousNode, currentNode);
				lemmasGraph.removeEdge(link);
				lemmasGraph.addEdge(new CustomLink(++link.weight, previousNode
						+ "-" + currentNode), previousNode, currentNode);

			}
			previous = current; // avoids a ConcurrentModificationException
		}

		Collection<CustomNode> nodes = new LinkedList<CustomNode>();
		nodes = lemmasGraph.getVertices();
		DegreeScorer<CustomNode> graphscorer = new DegreeScorer<CustomNode>(lemmasGraph);
		int score = 0;
		TreeMap<String, Integer> wordsScore = new TreeMap<String, Integer>();
		for (CustomNode node : nodes) {

			score = graphscorer.getVertexScore(node);
			wordsScore.put(node.word, score);

		}
		// return scored keywords
		return wordsScore;
	}
}
