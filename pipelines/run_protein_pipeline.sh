#!/bin/bash
#mvn -e compile exec:java -Dinput=/Users/roederc/work/data/medline/pubmed_batches/batch_pubmed_005533/  -Ddictionary=cmDict-GO.xml

mvn -e compile exec:java \
 -Dexec.mainClass=edu.ucdenver.ccp.nlp.pipelines.ProteinPipeline \
 -Dexec.args=" $1 " > output.$1  & 
