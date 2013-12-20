#!/bin/bash

# in-lieu of installing a jar into a local maven artifactory repository which may not exist
# this script installs it into the user's local maven repository: ~/.m2/repository

#	<groupId>pmc</groupId>
#			<artifactId>dtd2</artifactId>
#			<version>2.3</version>
#			<scope>system</scope>
#			<!--<systemPath>${basedir}/target/classes/pmc-dtd-2.3.jar</systemPath>-->
#			<systemPath>${basedir}/src/main/resources/pmc-dtd-2.3.jar</systemPath>

mvn install:install-file 	-Dfile=src/main/resources/pmc-dtd-2.3.jar\
							-DgroupId=pmc\
							-DartifactId=dtd2\
							-Dversion=2.3\
							-Dpackaging=jar

mvn install:install-file 	-Dfile=src/main/resources/pmc-dtd-3.0.jar\
							-DgroupId=pmc\
							-DartifactId=dtd3\
							-Dversion=3.0\
							-Dpackaging=jar
	
