#!/bin/bash
#mvn -e compile exec:java -Dinput=/Users/roederc/work/data/medline/pubmed_batches/batch_pubmed_005533/  -Ddictionary=cmDict-GO.xml
mvn -f service-pom.xml  -e compile exec:java -Dinput=/Users/roederc/work/data/medline/pubmed_batches/small/pmid_22730558.txt

