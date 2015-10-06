package fon.tags.graph;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class NounPhrases {

	public static List<String> GetNounPhrases(Tree parse)
	{
		//look in the tree for noun phrases based on the annotations NP
		List<String> phraseList=new ArrayList<String>();

		String phrase = "";
		ArrayList<Tree> list_trees = new ArrayList<Tree>();
		for (Tree subtree: parse)
		{
			//System.out.println(subtree.firstChild().numChildren());
			if(subtree.label().value().equalsIgnoreCase("NP") && subtree.firstChild().numChildren()==1 && !subtree.firstChild().label().value().equalsIgnoreCase("NP"))
			{
				list_trees.add(subtree);
				//phrase = "";
				StringBuilder stringBuilder = new StringBuilder();
				if (subtree.numChildren()>=2 && subtree.numChildren()<=4) {
					if (subtree.numChildren()==2 && subtree.firstChild().label().value().equalsIgnoreCase("DT" ) || subtree.firstChild().label().value().equalsIgnoreCase("PRP$")) {
					}
					else {
						for (int i = 0;i<subtree.numChildren();i++) {
							//System.out.println(subtree.firstChild().label().value());
							stringBuilder.append(subtree.getChild(i).firstChild().toString()+" ");
							//System.out.println(subtree.getChild(i).firstChild().toString()); 
						}
						phrase = stringBuilder.toString().trim();
						//System.out.println(phrase);
						phraseList.add(phrase.toLowerCase());
					}
				}

				//phraseList.add(subtree);	        
			}
		}

		return phraseList;

	}
}
