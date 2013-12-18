
JAR_LOCATION=/Users/roederc/work/git/ccp_nlp/doc/src/main/resources

mvn install:install-file \
-Dfile=$JAR_LOCATION/pmc-dtd-2.3.jar \
-DgroupId=edu.ucdenver.ccp \
-DartifactId=nlp.doc.pmc.dtd2 \
-Dpackaging=jar \
-Dversion=2.3

mvn install:install-file \
-Dfile=$JAR_LOCATION/pmc-dtd-3.0.jar \
-DgroupId=edu.ucdenver.ccp \
-DartifactId=nlp.doc.pmc.dtd3 \
-Dpackaging=jar \
-Dversion=3.0

mvn install:install-file \
-Dfile=$JAR_LOCATION/art502.jar \
-DgroupId=edu.ucdenver.ccp \
-DartifactId=nlp.doc.elsevier.dtd502 \
-Dpackaging=jar \
-Dversion=5.0.2 

mvn install:install-file \
-Dfile=$JAR_LOCATION/art510.jar \
-DgroupId=edu.ucdenver.ccp \
-DartifactId=nlp.doc.elsevier.dtd510 \
-Dpackaging=jar \
-Dversion=5.1.0

mvn install:install-file \
-Dfile=$JAR_LOCATION/art520.jar \
-DgroupId=edu.ucdenver.ccp \
-DartifactId=nlp.doc.elsevier.dtd520 \
-Dpackaging=jar \
-Dversion=5.2.0

