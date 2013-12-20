A minimalistic UIMA support library in the style of CCP. This project
includes some basic, simple, uima pipelines and analysis engines cribbed from
both UIMA and uimaFIT tutorials, as well as past CCP UIMA work. 

In a significant effort to simplify and make more accessible, this work 
breaks from past UIMA work at the CCP in that it does not include the
"util" layer of code originally used to isolate use of  UIMA. It fully
accepts and embraces UIMA as a standard for Java NLP. Other aspects
have also been left behind:
 * Importing Protege3 and Knowtator projects from the project files
   has been abandonded in favor of XML import that doesn't require
   the Protege API or complex code of the past.
 * The  UIMA_Util functions to move between the util objects and UIMA 
   objects has been abandonded. 
 * Without "util", the evolution of that architecture with a set of 
   base core interfaces and "wrapped" implementations, has also been 
   abandonded.
 * Shims and View support is not implemented here. Collection readers
   include necessary XML to plain-text conversion.

Extending the functionality of UIMA generated annotation classes 
remains an area of exploration. This was accomplished in past efforts,
largely through the use of static functions in the increasingly large 
UIMA_Util class. This work starts down a similar path using static
functions, but divides them up by annotation class extension to aid
devleopers in finding them. Utility functions for ClassMentions are
found in the ClassMention extenstion class, ClassMentionX for example.
Extending the classes was considered, but would require down casting.
Directly modifying the classes was also considered, but steps away
from easy type system modification and regeneration.

Chris Roeder, 2013

* ts - UIMA type system xml files and generated java

* analysis_engines
	** AnalysisEngineTest.java
	** Debug_AE.java
	** LingPipeSentenceDetector_AE.java
	** LingPipeSentenceDetectorAeTest.java
	** ClassMentionX.java
		An extension of the generated ClassMention class with a static function
		that might have been a member of ClassMention or ClassMentionX if it
		weren't for the fact you can't do type conversion as easily in Java
		or that UIMA is in control of what objects get created in the JCas.

* lingpipe_analysis_engines - a separate directory for lingpipe analysis engines to isolate the dependency on lingpipe and its license


* doc - an interface to a local install of medline, as well as additions to
	integrate pmc oa and a local elsevier  collection

* cr - a set of collection readers including DbCollectionReader and derivatives
	for using the doc directory's database

* util - a home for  utility classes
	** SpacedProperties - a wrapper on java properties that allows for namespaces in the names

* pipelines
	** BaseUimaFitPipeline.java - a base class using the UIMA base FileSystemCollectionReader

	** ProteinPipeline.java
		This is basically a type system modification over the RoomNumberAnnotator.
		The maven pom includes steps to generate and include for compilation the type system classes.
		It adds usage of the CASDumpWriter from uimafit.
		Protein.java
		ProteinAnnotator.java

	** ParameterExamplePipeline.java (TBD)
		We need a subtle modification to show parameters uimaFIT-style.

	** ConceptMapperPipeline.java 
		This set includes work from a number of people showing how to create a ConceptMapper
		Dictionary from an OBO file, then run CM in a pipeline to create RDF output.
		It uses xml files to describe the UIMA Analysis Engines.
		See: https://groups.google.com/forum/?fromgroups#!topic/uimafit-users/BiCdfJrwGBE

	** KnowtatorPipeline (in devleopment)

 
* ws - a web_service iniitally surfacing the concept_mapper pipeline

* rdf_document_interface - an earlier version of doc using triple stores

* sesame_interface - a connection factory for rdf_document_interface

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/51326b276ac97a08eefd1f7b010eea0e "githalytics.com")](http://githalytics.com/croeder/uima_sample)


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/croeder/uima_sample/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

