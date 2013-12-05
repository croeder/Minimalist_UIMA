ccp_nlp
===========

some basic, simple, uima pipelines and analysis engines cribbed from
both UIMA and uimaFIT tutorials. Some requires code from here:
http://sourceforge.net/projects/bionlp-uima/files/ccp-nlp/v3.1/

Chris Roeder, Nov. 2012

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

* concept_mapper_pipelines

	** GetStartedQuickAE.java
		This is a pipeline-less AE with direct JCAS creation and engine
		running. It's about as simple as you can get.

	** BaseUimaFitPipeline.java
		This one is the basic RoomNumberAnnotator code from the UIMA tutorial.
		It adds a pipeline, driven here by uimaFIT.:w
		RoomNumber.java
		RoomNumberAnnotator.java

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

* lucene_pipelines
 
* web_service
	** a web_service iniitally surfacing the concept_mapper pipeline

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/51326b276ac97a08eefd1f7b010eea0e "githalytics.com")](http://githalytics.com/croeder/uima_sample)


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/croeder/uima_sample/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

