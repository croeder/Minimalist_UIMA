#!/bin/bash
mvn -e compile exec:java\
 -Dexec.mainClass=edu.ucdenver.ccp.nlp.pipelines.ConceptMapperPipeline \
 -Dexec.args=" $1 " | grep dictTerm


