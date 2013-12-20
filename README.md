
some basic, simple, uima pipelines and analysis engines cribbed from
both UIMA and uimaFIT tutorials. Some requires code from here:
http://sourceforge.net/projects/bionlp-uima/files/ccp-nlp/v3.1/

Chris Roeder, Nov. 2012

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

