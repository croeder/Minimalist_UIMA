#!/bin/bash
mvn -e compile exec:java\
 -Dexec.mainClass=edu.ucdenver.ccp.nlp.pipelines.DMapPipeline \
 -Dexec.args=" $1 " > output.$1  


