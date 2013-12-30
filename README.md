A minimalistic UIMA support library in the uimaFIT style of CCP. This project
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


unfinished with apologies
Chris Roeder, 2013


ccp_nlp submodules:

* ts - UIMA type system xml files and generated java

* ae 
	** AnalysisEngineTest.java
	** Debug_AE.java
	** LingPipeSentenceDetector_AE.java
	** LingPipeSentenceDetectorAeTest.java
	** ClassMentionX.java, SlotMentionX.java
		Extensions of the generated ClassMention and SlotMention  classes with static functions
		that might have been a member of ClassMention or ClassMentionX if it
		weren't for the fact you can't do type conversion as easily in Java
		or that UIMA is in control of what objects get created in the JCas.
	** ClassMentionRemovalFilter_AE.java
	** ConceptMapper2CCPTypeSystemConverter_AE.java
	** DbInsert_AE.java - for use with backend
	** Knowtator_AE.java (incomplete)
	** MapNameToIDSlot_AE.java
	** opendmap/OpenDMAP_AE.java
	** Protein_AE.java (simple toy example)

* lingpipe_analysis_engines - a separate directory for lingpipe analysis engines to isolate the dependency on lingpipe and its license

* doc - an interface to a local install of medline, as well as additions to
	integrate pmc oa and a local elsevier  collection
	** Code to do a 2-stage parse of fulltext provided in XML. The first stage
	   applies an XSL stylesheet to convert between the source's (complex) XML
       to a more simple XML style.  The second stage parses that and creates
	   various annotations for zoning and italics.
		*** XsltConverter.java
		*** CcpXmlParser.java
		*** ElsevierArt5DtdClasspathResolver.java
		*** PmcDtdClasspathResolver.java
		*** PmcDtdUriResolver.java
	** Code to access the database and provide service for UIMA collection readers
		*** DbConnect.java
		*** DocumentProvider.java
		*** DocumentProviderFactory.java
		*** ElsevierArt5DocumentProvider.java
		*** MedlineDocumentProvider.java
		*** PmcDocumentProvider.java
	** ORM (JPA) code as well...

* backend - an interface to tables in the same database as doc, above, but
	focussed on storing the results of a pipeline run

* cr - a set of collection readers including DbCollectionReader and derivatives
	for using the doc directory's database
	** CcpXmlAnnotationFactory.java - a class for bridging from simple data from the doc module
		to uima annotations
	** DbCollectionReader.java - a base class for collection readers driven by a database to
		reference either abstracts in the database, such as the Medline CR, or to reference 
  		full-text paths such as the Pmc and Elsevier implementations.
	** ElsevierArt5DbCollectionReader.java
	** MedlineDbCollectionReader.java
	** PmcOaDbCollectionReader.java

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

	** ConceptMapperPipeline.java 
		This set includes work from a number of people showing how to create a ConceptMapper
		Dictionary from an OBO file, then run CM in a pipeline to create RDF output.
		It uses xml files to describe the UIMA Analysis Engines.
		See: https://groups.google.com/forum/?fromgroups#!topic/uimafit-users/BiCdfJrwGBE
        
		Running ConceptMapper from a uimaFIT universe hasn't been cracked here yet. This code
        falls back on XML descriptor files and modifying the resulting in-memory descriptor
	    instead of creating the in-memory descriptor directly from factory calls and 
        no xml files. Adapting concept-mapper is awkward because, as far as I'm aware, at the
        time of this writing, to have an analysis engine use uiamFIT parameters requires
        that it inherit from a uimaFIT version of JCasAnnotator_ImplBase instead of the 
        UIMA version. Future work would involve finding direct access to the initialization
        code there and calling it from the init method of a class that derives from ConceptMapper.
        This avoids modifying ConceptMapper source, though that would certainly work too.

	** KnowtatorPipeline (in devleopment)
        Past knowtator work at the CCP uses the protege API to access the pins/pont/pprj files.
        Work has begun on code to read exported XML files. While this avoids the Protege API
        code, it also avoids older CCP code that interfaces with it.

	** DMapPipeline.java

	** OpenNlpPipeline.java
 
* ws - (work in progress)a web_service iniitally surfacing the concept_mapper pipeline

* rdf_document_interface - an earlier version of doc using triple stores (abandoned)

* sesame_interface - a connection factory for rdf_document_interface (unused, perhaps useful elswhere)

