#		DBPEDIA TOPIC EXTRACTION



##Overview :

DbPedia Topic Extraction is a tool designed to extract relevant topics from an article .The tools makes use of DbPedia resources(dbpedia-spotlight) and also the bag-of-words model . 

##Concept of Working :

Here , each article ,whose topics has to be extracted is read . Once read ,stop-words are removed ,and the bag-of-words model gives the most repetitively occurring words in the articles. The bag-of-words model is built using Stanford Core NLP package .
Meanwhile , the article is also passed to DbPedia , which annotates the data and gives all the concepts in the given article. The concepts obtained are filtered . 
Next , the bag-of-words & the concepts from DbPedia are clubbed together . Here ,the second stage filtering takes place . Finally , we get a list of topics(concepts) which represent the given article .

##Assumptions: 

Most repetitive words are the one with high importance in the document.

##TODO :

* Providing ranking/score to each topic(concept) extracted.

* Improve Ontology features .

* Derive relation between extracted topics so as to obtain Article Summary .
