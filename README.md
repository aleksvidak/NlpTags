# NLP Tags
A keywords & keyphrases extraction Java RESTful Web Service for Java web applications.
## Overview
Code in this project is a result of research on natural language processing (NLP). Core functionalities are based on Stanford CoreNLP framework (http://nlp.stanford.edu/software/corenlp.shtml) and JUNG (http://jung.sourceforge.net/).

Main goal during project development was to create a functional Java Web Service to provide users the possibility to extract keywords and keyphrases from the given text and return result in JSON format.

It is still work in progress and it is not something that can be useful to public yet.

### Why?
Increasingly, companies, governments and individuals are faced with large amounts of text that are critical for working and living. Decisions need to be made fast, so it is crucial to get important parts of text in order to act accordingly. 

At the same time, a plethora of content recommendation systems are built and massive amount of Internet moguls tend to use 
them to get advantage of user feedback on various websites. 

This project has been built with the attempt to take a piece of text and convert it to programmer friendy data structure that
can be manipulated in different ways.

### About the Project
The idea to work on this kind of project was influenced by the __Lahiri, S., Choudhury, S. R., & Caragea, C. (2014). *Keyword and Keyphrase Extraction Using Centrality Measures on Collocation Networks*. CoRR. Retrieved November 22, 2014__ paper, from http://arxiv.org/pdf/1401.6571v1.

The approach in mentioned paper is based on several fixed steps on the way of retrieving keywords & keyphrases, so similarly is this project.

There are two parts of the project, one concerning extraction of keywords and other concerning extraction of keyphrases.

#### To extract keywords from text following steps are required:
1. Division of provided text into sentences and subsequently sentences into words,
2. Conversion of words into their basic form ([lemmatisation](https://en.wikipedia.org/wiki/Lemmatisation)),
3. Elimination of lemmas that would affect negativelly the final outcome, but empirically don't have value as keywords:
  * Top 5% and bottom 5% words ordered by frequency in the given text or
  * Most common english words (stopwords),
4. Creation of graph where lemmas represent nodes and number of immediate bigrams in text represents graph edge between two bigram lemmas,
5. Scoring the graph based on one of the given centrality measures,
6. Returning the scored lemmas in JSON format.

#### To extract keyphrases from text following steps are required:
1. Division of provided text into sentences and subsequently sentences into words,
2. Conversion of words into their basic form (lemmatisation),
3. Working out the grammatical structure of sentences by annotating lemmas,
4. Elimination of grammatical structures that don't represent noun phrases,
5. Creation of graph where noun phrases represent nodes and number of occurences of two noun phrases in certain window in text represents graph edge between two nodes.
6. Scoring the graph based on one of the given centrality measures,
7. Returning the scored noun phrases in JSON format.

### Solution
Based on the steps mentioned above, it was important to find appropriate libraries, one that deals with NLP tasks and other that is good for creating graphs and scoring edges based on the the different centrality measures.

To proccess input text Java Service uses [The Stanford CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml#Usage) library that provides a set of natural language analysis tools which can take raw text input and supply:
- the base forms of words, 
- their parts of speech, whether they are names of companies, people, etc., 
- normalized dates, times, and numeric quantities, and 
- marking up the structure of sentences in terms of phrases and word dependencies, 
- indication which noun phrases refer to the same entities, 
- indication of sentiment, etc. 

## References
- http://arxiv.org/abs/1401.6571
- http://nlp.stanford.edu/software/corenlp.shtml#Usage
- http://nlp.stanford.edu/software/lex-parser.shtml
- https://github.com/jconwell/coreNlp
- http://jung.sourceforge.net/
- http://programcreek.com/java-api-examples/index.php?example_code_path=weka-weka.core-Stopwords.java
- http://www.ling.upenn.edu/courses/Fall_2007/ling001/penn_treebank_pos.html
