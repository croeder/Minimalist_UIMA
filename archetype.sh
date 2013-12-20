#!/bin/sh

# see:
# https://jersey.java.net/documentation/latest/getting-started.html

mvn archetype:generate \
-DarchetypeArtifactId=jersey-quickstart-grizzly2 \
-DarchetypeGroupId=org.glassfish.jersey.archetypes  \
-DinteractiveMode=false \
-DgroupId=com.croeder.uima_sample.web_service \
-DartifactId=web_service \
-Dpackage=com.croeder.uima_sample.web_service \
-DarchetypeVersion=2.1
