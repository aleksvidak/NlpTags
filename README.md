# NlpTags
Code in this project is a result of research on natural language processing. It is based on Stanford CoreNLP framework and other helpful projects found on the internet regarding this subject.
It is still work in progress and it is not something that can be useful to public yet.

At the moment this project evolved to Java Web Service that provides users ability to choose a text they want, upload it and call web service to extract keywords or keyphrases from it with the number of keywords/keyphrases the one wants.

# WORK
My work on this project helped me to grow as Java programmer. I learned a lot about data structures and overcame some of the problems along the way regarding mostly extraction of keyphrases. 
Keywords extraction is based on the fixed steps:
1. provided text needs to be chunked into words, 
2. words are turned into their basic form (lemmatization),
3. elimination of frequent words (or most common english words),
5. lemmas become nodes connected with weighted edges (number of immediate bigrams in text)
4. lemmas in the graph are scored based on the degree centrality measure

Keyphrases extraction is based on:
1. chunking provided text into words, 
2. work out the grammatical structure of sentences with lex parser for getting noun phrases
3. using window (median sentence length in text) approach to elaborate how many times two noun phrases are together in a text
4. phrases are presented in the form of graph and are scored based on the degree centrality measure

# References
- http://arxiv.org/abs/1401.6571
- http://nlp.stanford.edu/software/corenlp.shtml#Usage
- http://nlp.stanford.edu/software/lex-parser.shtml
- https://github.com/jconwell/coreNlp
- http://jung.sourceforge.net/
- http://programcreek.com/java-api-examples/index.php?example_code_path=weka-weka.core-Stopwords.java
- http://www.ling.upenn.edu/courses/Fall_2007/ling001/penn_treebank_pos.html
