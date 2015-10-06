package fon.tags.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

public class WordsGraph {

	// Lemmas become nodes connected with weighted edges (number of immediate bigrams in text)

	public static HashMap<String, Integer> CreateGraph(List<String> dictionary) {
		Graph<MyNode, MyLink> lemmasGraph = new UndirectedSparseMultigraph<MyNode, MyLink>();

		String previous = "";
		//create graph
		for (String current : dictionary) {
			int c = 1;

			MyNode currentNode = new MyNode(current);
			MyNode previousNode = new MyNode(previous);

			if (!lemmasGraph.containsVertex(currentNode)) {
				lemmasGraph.addVertex(currentNode);
			}

			if (previous != ""
					&& lemmasGraph.findEdge(previousNode, currentNode) == null) {
				MyLink noviLink = new MyLink(c, previousNode + "-"
						+ currentNode);
				lemmasGraph.addEdge(noviLink, previousNode, currentNode);
			}

			else if (previous != ""
					&& lemmasGraph.findEdge(previousNode, currentNode) != null
					&& lemmasGraph.containsEdge(lemmasGraph.findEdge(previousNode,
							currentNode))) {
				MyLink link = lemmasGraph.findEdge(previousNode, currentNode);
				lemmasGraph.removeEdge(link);
				lemmasGraph.addEdge(new MyLink(++link.weight, previousNode + "-"
						+ currentNode), previousNode, currentNode);
				
			}
			previous = current; // avoids a ConcurrentModificationException
		}


		Collection<MyNode> nodes = new LinkedList<MyNode>();
		nodes = lemmasGraph.getVertices();
		DegreeScorer<MyNode> graphscorer = new DegreeScorer<MyNode>(lemmasGraph);
		int score = 0;
		HashMap<String, Integer> wordsScore = new HashMap<String, Integer>();
		for (MyNode node : nodes) {
		
		score = graphscorer.getVertexScore(node);
		wordsScore.put(node.word, score);
		
		}
		//return scored keywords
		return wordsScore;
	}
}
