#!/usr/bin/env bash
#/bin/bash

CORPUS_PATH="../../ir-corpus"
INDEX_PATH="../../news-index"
QUERY_OUTPUT_PATH="$CORPUS_PATH/qrels"
QREL_COMPARE_PATH="$CORPUS_PATH/qrels.assignment2.part1"
TOPICS_PATH="$CORPUS_PATH/topics"

SCORING_LIST=(bm25 tfidf multisimilarity customBM25)
ANALYZER_LIST=(englishAnalyzer standardAnalyzer teamScoboCustomAnalyzerWithStopwords teamScoboCustomAnalyzer scobo-analyzer)

echo "Starting result gathering"

for analyzer in ${ANALYZER_LIST[*]}
do
	for scoring in ${SCORING_LIST[*]}
	do
		INDEX_EXEC_ARGS="-scoring $scoring -analyzer $analyzer -documents $CORPUS_PATH -index-output $INDEX_PATH"

		echo "Analyzer: $analyzer Scorer: $scoring"

		mvn exec:java -Dexec.mainClass="ie.tcd.lucene.scobo.Indexer" -DjvmArgs="-Xmx6g" -Dexec.args="$INDEX_EXEC_ARGS"

		echo "Done indexing"

		QUERY_EXEC_ARG="-scoring $scoring -analyzer $analyzer -index $INDEX_PATH -queries $TOPICS_PATH -output $QUERY_OUTPUT_PATH"

		mvn exec:java -Dexec.mainClass="ie.tcd.lucene.scobo.QueryRunner" -DjvmArgs="-Xmx6g" -Dexec.args="$QUERY_EXEC_ARG"

		echo "Done querying"

		RESULT_PATH="$CORPUS_PATH/results/${analyzer}_${scoring}.log"
		../../trec_eval/trec_eval $QREL_COMPARE_PATH $QUERY_OUTPUT_PATH > $RESULT_PATH

		echo "Completed trec_eval"
	done
done
