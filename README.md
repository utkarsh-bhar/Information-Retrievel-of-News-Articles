# Scobo

To build the project

```
cd scobo
mvn clean install
```

### Indexing 

Note, the current implementation for indexing requires atleast 6 GB of memory. If using the AWS instance, the index is already created under `~/new-index`

```
mvn exec:java -Dexec.mainClass="ie.tcd.lucene.scobo.Indexer" -DjvmArgs="-Xmx6g" -Dexec.args="-scoring multisimilarity -analyzer englishAnalyzer -documents ../news-corpus -index-output ../news-index"
```

### Running Query

```
mvn exec:java -Dexec.mainClass="ie.tcd.lucene.scobo.QueryRunner" -DjvmArgs="-Xmx6g" -Dexec.args="-scoring multisimilarity -analyzer englishAnalyzer -index ../news-index -queries ../topics -output ../qrels"
```

### TREC Eval

```
cd ~/trec_eval
./trec_eval ../qrels.assignment2.part1 ../qrels
```