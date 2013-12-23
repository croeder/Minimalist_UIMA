#!/bin/bash
mvn -e compile exec:java\
 -Dexec.mainClass=edu.ucdenver.ccp.nlp.pipelines.DMapPipeline \
 -Dexec.args=" $1 " 
### | grep SEMANTIC | grep -v token: | grep -v sentence:


