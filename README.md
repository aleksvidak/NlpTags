# NlpTags
Code in this project is a result of research on natural language processing. It is based on Stanford CoreNLP framework and other helpful projects found on the internet regarding this subject.
It is still work in progress and it is not something that can be useful to public yet.

At the moment this project evolved to Java Web Service that provides users ability to choose a text they want, upload it and call web service to extract keywords or keyphrases from it with the number of keywords/keyphrases the one wants.

# WORK
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

# References
- http://arxiv.org/abs/1401.6571
- http://nlp.stanford.edu/software/corenlp.shtml#Usage
- http://nlp.stanford.edu/software/lex-parser.shtml
- https://github.com/jconwell/coreNlp
- http://jung.sourceforge.net/
- http://programcreek.com/java-api-examples/index.php?example_code_path=weka-weka.core-Stopwords.java
- http://www.ling.upenn.edu/courses/Fall_2007/ling001/penn_treebank_pos.html
