/*
 Copyright (c) 2013, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.ae;

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
import org.apache.uima.examples.SourceDocumentInformation;

import edu.ucdenver.ccp.nlp.ts.IdDictTerm;
import edu.ucdenver.ccp.nlp.ts.Protein;
import edu.ucdenver.ccp.nlp.ts.DocumentInformation;

import edu.ucdenver.ccp.nlp.backend.orm.PrimitiveResult;
import edu.ucdenver.ccp.nlp.backend.orm.CompoundResult;
import edu.ucdenver.ccp.nlp.backend.ResultProvider;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.descriptor.SofaCapability;


import edu.ucdenver.ccp.nlp.ts.SentenceAnnotation;
import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;
import edu.ucdenver.ccp.nlp.ts.Annotator;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.tsx.ClassMentionX;
import edu.ucdenver.ccp.nlp.ts.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;
import edu.ucdenver.ccp.nlp.ts.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.ts.ComplexSlotMention;


@SofaCapability
public class DbInsert_AE extends JCasAnnotator_ImplBase {
    private final boolean DEBUG = false;
	private String[] classMentionNames = {
						"hates", "loves", "cell_type", "normalized_gene",
						"protein-list", "expression", "c-expression", "c-protein",
						"protein", "protein-list", "g-protein-list", "cell",
						"c-cell-type", "cell-list"
	};
	private Set<String> classMentionNameSet = new TreeSet<String>();

	ResultProvider provider = null;


    @Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		provider = new ResultProvider();
		classMentionNameSet.addAll(Arrays.asList(classMentionNames));
    }


    @Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String docId="<na>";
	
        FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex().iterator();
        while (annotIter.hasNext()) {

	        Annotation annot = (Annotation) annotIter.next();
			if (annot instanceof DocumentInformation) {
				DocumentInformation di = (DocumentInformation) annot;
				//System.out.println("XXXYY Doc. Info. \"" + di.getUri() + "\" id:" + di.getDocumentId());
				docId  = di.getUri();
			}
			else if (annot instanceof SourceDocumentInformation) {
				SourceDocumentInformation sdi = (SourceDocumentInformation) annot;
				//System.out.println("XXXYY Source Doc. Info. \"" + sdi.getUri() );
				docId  = sdi.getUri();
			}
			//else if (annot instanceof SentenceAnnotation) {
			//	System.out.println("SEMANTIC sentence: \"" + annot.getCoveredText() + "\"");
			//}
			else if (annot instanceof TextAnnotation) {
	
	        	TextAnnotation textAnnot = (TextAnnotation) annot;
				ClassMention cm = textAnnot.getClassMention();
	
				// TOKEN   ??? un-"normalized"??? WTF from CM tokenizer?
				//if (cm != null && cm.getMentionName().equals("token")) {
				//	IntegerSlotMention numberSlot = (IntegerSlotMention) ClassMentionX.getSlotMentionByName(cm,"tokenNumber");
				//	System.out.println("class mention token:" + textAnnot.getCoveredText() + " number: " + numberSlot.getSlotValues(0));
				//}	
	
				// ** SEMANTIC slots, DMAP stuff **
				if (cm != null && cm.getMentionName().equals("expression")) {
					PrimitiveResult cellResult = null;
					PrimitiveResult geneResult = null;
					PrimitiveResult agentResult = new PrimitiveResult("xx", "express trigger", 0,0,0, "", "");

					try {
						ComplexSlotMention cellSlot = ClassMentionX.getComplexSlotMentionByName(cm, "location");
						if (cellSlot != null) {
							ClassMention cellReferent = (ClassMention) cellSlot.getClassMentions(0);
							TextAnnotation cellTA = cellReferent.getTextAnnotation();
							StringSlotMention cellIdSlot =(StringSlotMention) ClassMentionX.getSlotMentionByName(cellReferent, "ID");
							cellResult = new PrimitiveResult("CL", cellIdSlot.getSlotValues().get(0), cellTA.getBegin(),cellTA.getEnd(),0, docId,"");
							//System.out.println("XXXYY cell" + cellIdSlot.getSlotValues().get(0));

							//String ontologyName, String ontologyId, int spanStart, int spanEnd, int sentenceNum, String docId, String idType) 
						}
						else { System.out.println(" XXXYY bogus cell_type" + cm.getMentionName()); }
					}
					catch (Exception x) {
						System.out.println(" XXXYY bogus cell_type" + x);
						x.printStackTrace();
					}

					try {
						ComplexSlotMention geneSlot = ClassMentionX.getComplexSlotMentionByName(cm, "protein");
						if (geneSlot != null) {
							ClassMention geneReferent = (ClassMention) geneSlot.getClassMentions(0);
							TextAnnotation geneTA = geneReferent.getTextAnnotation();
							StringSlotMention geneIdSlot = (StringSlotMention) ClassMentionX.getSlotMentionByName(geneReferent, "ID");
							geneResult = new PrimitiveResult("PR", geneIdSlot.getSlotValues().get(0), geneTA.getBegin(),geneTA.getEnd(),0, docId,"");
							//System.out.println("XXXYY gene" + geneIdSlot.getSlotValues().get(0));
						}
						else { System.out.println(" XXXYY bogus cell_type" + cm.getMentionName()); }
					}
					catch (Exception x) {
						System.out.println("XXXYY bogus normalized gene" + x);
						x.printStackTrace();
					}

					// agent, theme, location
					if (cellResult != null && geneResult != null) {
						CompoundResult expressionResult = new CompoundResult(agentResult, geneResult, cellResult);
						provider.insertCompoundResult(expressionResult);
					}
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
				DbInsert_AE.class, tsd
		);
	}
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(
				DbInsert_AE.class, tsd
		);
	}
	

}
