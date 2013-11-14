#!/bin/bash
mvn -e exec:java \
-Dexec.mainClass="com.croeder.uima_sample.OpenNlpPipeline" \
-Dexec.args="target/classes/input"
