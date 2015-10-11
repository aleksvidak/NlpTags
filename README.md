# NLP Tags
A keywords & keyphrases extraction Java RESTful Web Service for Java web applications.
## Overview
Code in this project is a result of research on natural language processing (NLP). Core functionalities are based on Stanford CoreNLP framework (http://nlp.stanford.edu/) and JUNG (http://jung.sourceforge.net/).

Main goal during project development was to create a functional Java Web Service to provide users the possibility to extract keywords and keyphrases from the given text and return result in JSON format.

It is still work in progress and it is not something that can be useful to public yet.

### Why?
Increasingly, companies, governments and individuals are faced with large amounts of text that are critical for working and living. Decisions need to be made fast, so it is crucial to get important parts of text in order to act accordingly. 

At the same time, a plethora of content recommendation systems are built and massive amount of Internet moguls tend to use 
them to get advantage of user feedback on various websites. 

This project has been built with the attempt to take a piece of text and convert it to programmer friendy data structure that
can be manipulated in different ways.

### About the Project
The idea to work on this kind of project was influenced by the __Lahiri, S., Choudhury, S. R., & Caragea, C. (2014). *Keyword and Keyphrase Extraction Using Centrality Measures on Collocation Networks*. CoRR. Retrieved November 22, 2014__ paper, that can be downloaded from http://arxiv.org/pdf/1401.6571v1.

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

#### Keywords
To deal with input text, Java Service uses [The Stanford CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml#Usage) library that provides a set of natural language analysis tools which can take raw text input and supply:
- the base forms of words, 
- their parts of speech, whether they are names of companies, people, etc., 
- normalized dates, times, and numeric quantities, and 
- marking up the structure of sentences in terms of phrases and word dependencies, 
- indication which noun phrases refer to the same entities, 
- indication of sentiment, etc. 

Process starts by chunking text to sentences and then a tokenizer divides text into a sequence of tokens, which roughly correspond to "words". 
Tokenizing text results are "words" which include dots, commas, regular words ... Tokens can be transformed to lemmas (to the words' basic form) and not all lemmas are valuable as tags in the end, so it is important to remove those unwanted lemmas:
- that are not nouns,
- that are less than 3 characters long,
- brackets that are presented in form of "-lrb-,-rrb-,-lsb-,-rsb-".

To be able to identify nouns there is MaxentTagger - a Part-Of-Speech Tagger (POS Tagger) which is a piece of software that reads text in some language and assigns parts of speech to each word, such as noun, verb, adjective, etc. There are two taggers in distribution and service uses a model using only left second-order sequence information and similar but less unknown words and lexical features as the other model in *edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger*. This tagger runs a lot faster, and is recommended for general use. Its accuracy was 96.92% on Penn Treebank WSJ secs. 22-24.

It assigns lemmas with one of the tags on the following [list](https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html). Needed noun tags are NN, NNS, NNP and NNPS. All other lemmas are ignored. 

This is the point in extraction of keywords where methods for eliminating __top 5% and bottom 5% words ordered by frequency in the given text_ and _most common english words (stopwords)__ separate ways.

##### Top 5% and bottom 5% words ordered by frequency in the given text
To remove words by their frequency in the given text it is important to make a dictionary where value is number of occurences of the specific word in text. After sorting that data structure it is possible to remove Top 5% and bottom 5% words by frequency.

##### Most common english words (stopwords)
To remove most common english words there are two stopword lists used in this project:
- http://programcreek.com/java-api-examples/index.php?example_code_path=weka-weka.core-Stopwords.java
- A list found in [StopAnalyzer](https://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/core/StopAnalyzer.html) class of Apache Lucene Core text search engine library.

The result of previous transformations is a list of lemmas ready for insertion into graph. To be able to create graph, its' nodes and edges, service uses [JUNG](http://jung.sourceforge.net/) library.
Iterating through list of lemmas, lemmas are added to the graph as nodes if the node doesn't exist already and every immediate bigram is presented in graph by edge between two lemmas, so every reoccurence of the same bigram increments the weight of the edge by one.
After graph creation nodes are scored using [DegreeScorer](http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/scoring/DegreeScorer.html) class from JUNG library which is based on degree centrality measure.

Keywords are returned to user based on the inquiry. It is possible to choose text for extraction, choose between extraction methods and number of keywords service should return (zero for all keywords).

Since there is a possibility to choose file from the filesystem, data is submitted to the server in the form of multipart/form-data. There is no query string in the url.

Example of service call:
```
POST /api/v1/tag
```
with parameters: file, method and number.

#### Keyphrases

### Acknowledgements

### Licence
