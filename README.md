# NLP Tags
A keywords & keyphrases extraction Java RESTful Web Service for Java web applications.
## Overview
Code in this project is a result of research on natural language processing (NLP). Core functionalities are based on Stanford CoreNLP framework (http://nlp.stanford.edu/software/corenlp.shtml) and JUNG (http://jung.sourceforge.net/).

Main goal during project development was to create a functional Java Web Service to provide users the possibility to extract keywords and keyphrases from the given text and return result in JSON format.

It is still work in progress and it is not something that can be useful to public yet.

### Why?
Increasingly, companies, governments and individuals are faced with large amounts of text that are critical for working and living. Decisions need to be made fast, so it is crucial to get important parts of text in order to act accordingly. 
At the same time, a plethora of content recommendation systems are built and massive amount of Internet moguls tend to use them to get advantage of user feedback. 
This project has been built with the attempt to take a piece of text and convert it to programmer friendy data structure that
can be manipulated in different ways.

## WORK


Keywords extraction is based on the fixed steps:
------
- provided text needs to be chunked into words, 
- words are turned into their basic form (lemmatization),
- elimination of frequent words (or most common english words),
- lemmas become nodes connected with weighted edges (number of immediate bigrams in text)
- lemmas in the graph are scored based on the degree centrality measure

Keyphrases extraction is based on:
------
- chunking provided text into words, 
- work out the grammatical structure of sentences with lex parser for getting noun phrases
- using window (median sentence length in text) approach to elaborate how many times two noun phrases are together in a text
- phrases are presented in the form of graph and are scored based on the degree centrality measure

## References
- http://arxiv.org/abs/1401.6571
- http://nlp.stanford.edu/software/corenlp.shtml#Usage
- http://nlp.stanford.edu/software/lex-parser.shtml
- https://github.com/jconwell/coreNlp
- http://jung.sourceforge.net/
- http://programcreek.com/java-api-examples/index.php?example_code_path=weka-weka.core-Stopwords.java
- http://www.ling.upenn.edu/courses/Fall_2007/ling001/penn_treebank_pos.html
