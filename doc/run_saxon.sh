#!/bin/bash

# http://www.saxonica.com/documentation/using-xsl/commandline.html
# https://www.oasis-open.org/committees/entity/spec-2001-08-06.html
# http://dtd.nlm.nih.gov/archiving/3.0/index.cgi?show=./catalog-v3.xml
# ftp://ftp.ncbi.nih.gov/pub/archive_dtd/archiving/2.3/
# http://dtd.nlm.nih.gov/archiving/3.0/index.cgi?show=.

SAXON_JAR=/Users/roederc/.m2/repository/com/saxonica/saxon9he/9.4.0.6/saxon9he-9.4.0.6.jar
RESOLVER_JAR=~/.m2/repository/xml-resolver/xml-resolver/1.2/xml-resolver-1.2.jar
INPUT=target/classes/ViewXMLXSLTConverter_AETest.xml
STYLESHEET=target/classes//PmcOpenAccess2.xsl
CATALOG=target/classes/pmc-dtd-2.3/catalog-v2.xml
java -cp $RESOLVER_JAR:$SAXON_JAR  \
	net.sf.saxon.Transform \
	-s:$INPUT \
	-xsl:$STYLESHEET \
	-t -T \
	-catalog:$CATALOG 
