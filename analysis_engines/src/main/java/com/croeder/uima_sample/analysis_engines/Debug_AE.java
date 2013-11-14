
package com.croeder.uima_sample.analysis_engines;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.cas.text.AnnotationIndex;

//import org.apache.uima.conceptMapper.DictTerm;
import com.croeder.uima_sample.annotation.IdDictTerm;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.descriptor.SofaCapability;


import com.croeder.uima_sample.annotation.SentenceAnnotation;
import com.croeder.uima_sample.annotation.TextAnnotation;
//import com.croeder.uima_sample.annotation.TokenAnnotation;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;
import com.croeder.uima_sample.annotation.Annotator;
import com.croeder.uima_sample.mention.ClassMention;
import com.croeder.uima_sample.mention_extensions.ClassMentionX;
import com.croeder.uima_sample.mention.IntegerSlotMention;
import com.croeder.uima_sample.mention.StringSlotMention;
import com.croeder.uima_sample.mention.PrimitiveSlotMention;
import com.croeder.uima_sample.mention.ComplexSlotMention;


@SofaCapability
public class Debug_AE extends JCasAnnotator_ImplBase {
    private final boolean DEBUG = false;
	private String[] classMentionNames = {
						"hates", "loves", "cell_type", "normalized_gene",
						"protein-list", "expression", "c-expression", "c-protein",
						"protein", "protein-list", "g-protein-list", "cell",
						"c-cell-type", "cell-list"
	};
	private Set<String> classMentionNameSet = new TreeSet<String>();


    @Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		classMentionNameSet.addAll(Arrays.asList(classMentionNames));
    }


    @Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

        //AnnotationIndex index = jcas.getAnnotationIndex(TextAnnotation.type);
        //FSIterator<Annotation> annotIter = index.iterator();
        FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex().iterator();
        while (annotIter.hasNext()) {

	        Annotation annot = (Annotation) annotIter.next();
			if (annot instanceof TokenAnnotation) {
				System.out.println("SEMANTIC token: " + annot.getBegin() + ", " + annot.getEnd() + " \"" + annot.getCoveredText() + "\"");
			}
			if (annot instanceof IdDictTerm) {
				IdDictTerm dt = (IdDictTerm) annot;
				System.out.println("SEMANTIC dictTerm (ConceptMapper):\"" 
					+ " canon:" + dt.getDictCanon() + "\"" 
					+ " (" + annot.getBegin() + ", " + annot.getEnd() + ")"
					+ " covered:  \"" + annot.getCoveredText() + "\""
					+ " matched: \"" + dt.getMatchedText() + "\"" 
					+ " id: \"" + dt.getId() + "\"");
			}
			else if (annot instanceof TextAnnotation) {
	
	        	TextAnnotation textAnnot = (TextAnnotation) annot;
	
				if (textAnnot instanceof TextAnnotation) {
					Annotator annotator = textAnnot.getAnnotator();
					if (annotator != null) {
						System.out.println("Annotator:"
						+ " First Name: " + annotator.getFirstName()
						+ " Last Name: " + annotator.getLastName()
						+ " Affiliation: " + annotator.getAffiliation());
					}
					else {
						System.out.println(" NO ANNOTATOR " ); // TODO logger
					}
				}
	
				// SENTENCE
				if (textAnnot instanceof SentenceAnnotation) {
					System.out.println("SEMANTIC sentence: \"" + textAnnot.getCoveredText() + "\"");
				}
				else if (annot instanceof TextAnnotation) {
					TextAnnotation ta =  (TextAnnotation) annot;
					ClassMention cm = ta.getClassMention();
					if (cm == null) {
						try {
							System.out.println("NULL ClassMention for TA class:" + ta.getClass().getName() + " with covered=" + ta.getCoveredText());
						}
						catch (Exception x)  {
							System.out.println("This TA is really hosed");
						}
					}
				
	
					// TOKEN  ****** NEVER GONNA HAPPEN*************** check for TokenAnnotation, but that won't happen in many pipelines unless the right tokenizer is used
					if (DEBUG && cm != null && cm.getMentionName().equals("token")) {
						IntegerSlotMention numberSlot = (IntegerSlotMention) ClassMentionX.getSlotMentionByName(cm,"tokenNumber");
						System.out.println("token:" + ta.getCoveredText() + " number: " + numberSlot.getSlotValues(0));
					}	
	
	
					// ** SEMANTIC slots **
					else if (cm != null && classMentionNameSet.contains(cm.getMentionName().toLowerCase())) {
						System.out.print("SEMANTIC: name:\"" + cm.getMentionName() + "\" covered: \"" +  ta.getCoveredText() + "\"" );
	
						Collection<String> slotNames;
						slotNames =  ClassMentionX.getComplexSlotMentionNames(cm);
						if (slotNames != null && slotNames.size() > 0) {
							for (String slotName : slotNames) {
								System.out.println("");
								ComplexSlotMention complexSlot = ClassMentionX.getComplexSlotMentionByName(cm, slotName);
								ClassMention referent = (ClassMention) complexSlot.getClassMentions(0);
								PrimitiveSlotMention pSlot =(PrimitiveSlotMention) ClassMentionX.getSlotMentionByName(referent, slotName);
								System.out.print("  complex slot:" + slotName + " refers to " + referent.getMentionName());
								if (pSlot != null && pSlot instanceof StringSlotMention ) {
									System.out.print(" has value: " + ((StringSlotMention) pSlot).getSlotValues().get(0) );						
								}
								
							}
						}
	
						Collection<String> primitiveSlotNames =  ClassMentionX.getPrimitiveSlotMentionNames(cm);
						if (primitiveSlotNames != null && primitiveSlotNames.size() > 0) {
							for (String slotName : primitiveSlotNames) {
								System.out.print("  slot:" + slotName + " "); 
								PrimitiveSlotMention pSlot = ClassMentionX.getPrimitiveSlotMentionByName(cm, slotName);
								if (pSlot instanceof StringSlotMention) {
									System.out.print(" has value: " + ((StringSlotMention) pSlot).getSlotValues().get(0) );						
								}
							}
						}
					}
	
	
					// OTHER
					else if (cm != null) { //DEBUG) {
						System.out.println("unknown mention: " + cm.getMentionName());
						Collection<String> slotNames =  ClassMentionX.getPrimitiveSlotMentionNames(cm);
						for (String slotName : slotNames) {
							System.out.print(slotName 
									//+ UIMA_Util.getFirstSlotValue((StringSlotMention)UIMA_Util.getPrimitiveSlotMentionByName(cm, slotName))
								);
						}
	
						slotNames =  ClassMentionX.getComplexSlotMentionNames(cm);
						for (String slotName : slotNames) {
							System.out.print(slotName + " "); 
						}
					}
	
					System.out.println("");
				}
			}
		}

    }

    @Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
        super.collectionProcessComplete();
    }
    
    
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
				Debug_AE.class, tsd
		);
	}
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(
				Debug_AE.class, tsd
		);
	}
	
	

}
