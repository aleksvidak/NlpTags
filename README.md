# NLP Tags
A keywords & key-phrases extraction RESTful Web Service written in Java.
## Overview
The code in this project is based on the published research results in the field of Natural Language Processing (NLP). Core functionalities are based on Stanford CoreNLP framework (http://nlp.stanford.edu/) and JUNG (http://jung.sourceforge.net/).

The project's main goal was to create a functional RESTful Web Service to provide users with the possibility to extract keywords and key-phrases from the text and return results in JSON format. The extraction of keywords and key-phrases is based on graph-based representation of text and application of Social Network Analysis (SNA) metrics.

It is still work in progress.

### Why?
Increasingly, companies, governments and individuals are faced with large amounts of text that are critical for their everyday working and life practices. Decisions need to be made fast, so it is crucial to get important information from text to act accordingly. 

At the same time, a plethora of content recommendation systems are built and massive amount of Internet moguls tend to use 
that same information from text to get advantage of user feedback on various websites. 

This project has been built with as an attempt to provide a service that takes a piece of text, extracts the most relevant keywords and key-phrases from it and returns them in a programming friendly data structure that can be manipulated in different ways.

### About the Project
The work done in this project is largely based on the research reported in *Lahiri, S., Choudhury, S. R., & Caragea, C. (2014). Keyword and Keyphrase Extraction Using Centrality Measures on Collocation Networks. CoRR. Retrieved November 22, 2014*, that can be downloaded from http://arxiv.org/pdf/1401.6571v1.

The approach in the mentioned paper consists of several steps that lead to the extraction of keywords and key-phrases from any piece of text; these steps are closely followed in this project.

The project consists of two parts, one concerning the extraction of keywords and other concerning the extraction of key-phrases.

#### To extract keywords from text, the following steps are required:
1. Division of the provided text into sentences, and subsequently sentences into words
2. Conversion of words into their basic form ([lemmatisation](https://en.wikipedia.org/wiki/Lemmatisation))
3. Elimination of lemmas that have empirically been proven as irrelevant for keywords extraction and thus might negatively affect the final outcome:
  * 5% of the most frequent and 5% of the least frequent lemmas (i.e., top 5% and bottom 5% of lemmas ordered by their frequency) in the given text or
 * Most common English words (stop-words)
4. Creation of graph where lemmas represent nodes, while an edge between two nodes is established if the corresponding lemmas occur one next to the other in the input text (i.e., if they form a bigram); edges are weighted based on the number of immediate bigrams in the input text
5. Scoring the graph based on one of the given centrality measures (Degree, Strength, Neighborhood size – order 1, Coreness, Clustering Coefficient, Structural Diversity Index, PageRank, HITS, Betweenness, Closeness, Eigenvector Centrality)
6. Returning the scored lemmas in JSON format.

#### To extract key-phrases from text, the following steps are required:
1. Division of the provided text into sentences, and subsequently sentences into words
2. Conversion of words into their basic form (lemmatisation)
3. Working out the grammatical structure of sentences by annotating lemmas with Part of Speech (POS) tags
4. Elimination of grammatical structures that do not represent noun phrases
5. Creation of a graph where noun phrases represent nodes, while edges are formed between two nodes if the corresponding noun phrases occur in a specified 'window' in the input text; edges are weighted based on the number of common occurrences of the two noun phrases in the text
6. Scoring the graph based on one of the given centrality measures (Degree, Strength, Neighborhood size – order 1, Coreness, Clustering Coefficient, Structural Diversity Index, PageRank, HITS, Betweenness, Closeness, Eigenvector Centrality)
7. Returning the scored noun phrases in JSON format.

### Solution
Based on the steps mentioned above, it was important to find appropriate software libraries, those that deal with NLP tasks and others that are good for creating graphs and scoring edges based on different SNA centrality measures.

#### Keywords
To process the input text, the project uses [the Stanford CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml#Usage) library that provides a set of natural language analysis tools that can take raw input text and supply:
- the base forms of words, 
- parts of speech tags,
- identification of named entities such as companies, people, etc., 
- normalized dates, times, and numeric quantities,  
- marking up the structure of sentences in terms of phrases and word dependencies, 
- co-reference resolution (i.e., indication which noun phrases refer to the same entities), 
- indication of sentiment, etc. 

The process starts by chunking the input text into sentences, and then a tokenizer divides text into a sequence of tokens, which roughly correspond to "words". 
Tokenization of text produces basic text units - tokens - which include dots, commas, regular words ... Tokens can be transformed to lemmas (to the words' basic form). However, not all lemmas are valuable as keywords in the end, so it is important to remove lemmas that are:
- not nouns,
- less than 3 characters long,
- brackets presented in form of "-lrb-,-rrb-,-lsb-,-rsb-,-lcb-,-rcb-".

For identifying nouns, CoreNLP provides MaxentTagger - Part-Of-Speech Tagger (POS Tagger). It is a piece of software that reads text in some language and assigns parts of speech to each word, such as noun, verb, adjective, etc. There are two taggers in distribution and service uses a model able to learn probabilities of POS tags for triples going through sequences in one direction (left to right) *english-left3words-distsim.tagger*. This tagger runs a lot faster than the bidirectional ones, and is recommended for general use. Its accuracy was 96.92% on Penn Treebank WSJ secs. 22-24.

It associates each lemma with one of the POS tags from the following [list](https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html). For this project, noun phrases tags -  NN, NNS, NNP and NNPS - are important; all other lemmas are ignored. 

This is the point in the process of extracting keywords where the noun phrases (selected in the previous step) should be filtered, to remove those that have very little likelihood of being keywords. This filtering can be done in two ways: 
1. by eliminating 5% of the most frequent and 5% of the least frequent words (i.e., top 5% and bottom 5% words ordered by their frequency) in the given text and
2. by eliminating common English words using one of the available stop-words list.

##### Removing top 5% and bottom 5% words ordered by their frequency in the given text
To remove words by their frequency in the given text it is important to make a dictionary of the words appearing in the text, and where each word is associated with the number of its occurrences in the text. After sorting thus obtained data structure, it is possible to remove top 5% and bottom 5% words ordered by their frequency.

##### Removing most common English words (stopwords)
To remove most common English words, two  publicly available stopword lists are used in this project:
- [Stopwords list from the Weka framework](http://programcreek.com/java-api-examples/index.php?example_code_path=weka-weka.core-Stopwords.java)
- A list provided in the [StopAnalyzer](https://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/core/StopAnalyzer.html) class of the Apache Lucene Core text search engine library.

The result of the above described processing steps is a list of lemmas ready for creation of a graph. To create a graph, project uses the [JUNG](http://jung.sourceforge.net/) Java library.
Iterating through list of lemmas, a lemma is added to the graph as new node if it hasn't been already added. Every bigram is presented in the graph by establishing an edge between the two lemmas that form the bigram, while every reoccurrence of the same bigram increments the weight of the corresponding edge by one.
After the graph creation, nodes are scored using [DegreeScorer](http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/scoring/DegreeScorer.html) class from the JUNG library; this scoring is based on degree centrality measure, which is defined as the number of links incident upon a node (i.e., the number of ties that node has).

The developed service regarding keywords allows for specifying:
- the text to be used for keywords extraction,
- the extraction methods to be applied,
- the number of keywords to be returned (zero for all keywords).

It is possible to make the call to the service using query string containing three parameters:
- text - text of the document you want to extract keywords from;
- method - two choices possible: 
 1. stopwords (extraction of keywords while pre-eliminating stopwords in the given text) or
 2. frequency (extraction of keywords while pre-eliminating top 5% and bottom 5% words ordered by their frequency in the given  text).
- number - number of keywords/key-phrases service will return (0 for all).

Example of service call:
```
GET /api/v1/tag?text=This is a keywords test.&method=stopwords&number=10
```
with parameters: text, method and number.

#### Key-phrases
As in the keywords extraction part of the project, in order to process the input text for key-phrases extraction, the project uses [the Stanford CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml#Usage) library.

The process starts by chunking the input text into sentences, and then a tokenizer divides text into a sequence of tokens. 
Tokenization of text produces basic text units - tokens - which include dots, commas, regular words ... Tokens can be transformed to lemmas (to the words' basic form). However, not all lemmas are valuable as keywords in the end, so it is important to remove lemmas that are:
- not nouns,
- less than 3 characters long,
- brackets presented in form of "-lrb-,-rrb-,-lsb-,-rsb-,-lcb-,-rcb-".

Next step in extracting key-phrases is putting all tokens that pass conditions above to a list. The list is parsed by [LexParser](http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/parser/lexparser/LexicalizedParser.html), a class that provides the top-level API and command-line interface to a set of reasonably good treebank-trained parsers. A natural language parser is a program that works out the grammatical structure of sentences, for instance, which groups of words go together (as "phrases") and which words are the subject or object of a verb. After parsed, an example sentence "This is one good example of a sentence." looks like: 
```
(ROOT
   (S
     (NP (DT This))
     (VP (VBZ is)
       (NP
         (NP (CD one) (JJ good) (NN example))
         (PP (IN of)
           (NP (DT a) (NN sentence)))))
     (. .)))
```
This is the point where the parsed sentence is processed and all the nodes that don't have the NP (Noun Phrase) tag are filtered out.
A collocation network in form of a graph is constructed for each document as follows: nodes represent unique noun phrases, and edges link together noun phrases that occur within a specific window of each other. Window size is the median sentence length of a document. Note that the edges were all weighted with the co-occurrence frequency of np1 and np2. While merging edges, edge weights were incremented.
After the graph creation, nodes are scored using [DegreeScorer](http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/scoring/DegreeScorer.html) class from the JUNG library; this scoring is based on degree centrality measure.

The developed service regarding key-phrases allows for specifying:
- the text to be used for key-phrases extraction,
- the extraction method to be applied,
- the number of key-phrases to be returned (zero for all key-phrases).

It is possible to make the call to the service using query string containing three parameters:
- text - text of the document you want to extract keywords from;
- method - 'keyphrases' (extraction of key-phrases);
- number - number of key-phrases service will return (0 for all).

Example of service call:
```
GET /api/v1/tag?text=This is a keyphrases test.&method=keyphrases&number=10
```
with parameters: text, method and number.

### Acknowledgements
This project has been developed as a part of the assignment for the subject *Applications of Artificial Intelligence* at the Faculty of Organizational Sciences, University of Belgrade, Serbia.
### License
See the [LICENSE](LICENSE.md) file for license rights and limitations (Apache License).
