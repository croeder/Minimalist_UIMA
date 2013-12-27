#!/bin/bash
echo "1: $1   2: $2"
mvn -e compile exec:java\
 -Dexec.mainClass=edu.ucdenver.ccp.nlp.pipelines.DMapPipeline \
 -Dexec.args=" $1 $2" \

##|  grep "SEMANTIC: name:" | grep -v cell_type | grep -v normalized_gene


