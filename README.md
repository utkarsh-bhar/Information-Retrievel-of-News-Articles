# Scobo

To build the project

```
cd scobo
mvn clean install
```

### Indexing 

```
mvn exec:java -Dexec.mainClass="ie.tcd.lucene.scobo.Indexer" -DjvmArgs="-Xmx6g" -Dexec.args="-scoring bm25 -analyzer englishAnalyzer -documents ../news-corpus -index-output ../news-index"
```

### Running Query

```
mvn exec:java -Dexec.mainClass="ie.tcd.lucene.scobo.QueryRunner" -DjvmArgs="-Xmx6g" -Dexec.args="-scoring bm25 -analyzer englishAnalyzer -index ../news-index -queries ../topics -output ../qrels"
```