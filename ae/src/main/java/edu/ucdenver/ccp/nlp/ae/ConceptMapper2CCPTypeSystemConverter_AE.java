/*
 * ConceptMapper2CCPTypeSystemConverter.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package edu.ucdenver.ccp.nlp.ae;

import java.util.ArrayList;
import java.util.List;

// import uima.tt.TokenAnnotation; conflicts with local class of same name

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;

import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.nlp.ts.IdDictTerm;

import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.Annotator;
import edu.ucdenver.ccp.nlp.ts.Span;
import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.ts.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;

/**
 * Converts IdDictTerm annotations used to interface to ConceptMapper to a corresponding TextAnnotation object.
 */
public class ConceptMapper2CCPTypeSystemConverter_AE extends JCasAnnotator_ImplBase {

	final String CANONICAL_NAME="canonical name";
	final String TOKEN="token";
	final String TOKEN_NUMBER="tokenNumber";

	/**
	 * Cycle through all OntologyTerms and uima.tt.TokenAnnotations and converts to TextAnnotations.
	 * OntologyTerm and TokenAnnotation annotations are removed from the CAS once converted.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		List<TextAnnotation> annotations2add = new ArrayList<TextAnnotation>();
		List<Annotation> annotations2remove = new ArrayList<Annotation>();

		for (IdDictTerm term : JCasUtil.select(jcas, IdDictTerm.class)) {
			TextAnnotation ta = convertOntologyTerm(term, jcas);
			if (ta != null) {
				annotations2add.add(ta);
				annotations2remove.add(term);
			}
		}

		int tokenNumber = 0;
		for (uima.tt.TokenAnnotation token : JCasUtil.select(jcas, uima.tt.TokenAnnotation.class)) {
			TextAnnotation ta = convertToken(token, jcas, tokenNumber);
			tokenNumber++;
			if (ta != null) {
				annotations2add.add(ta);
				annotations2remove.add(token);
			}
		}

		for (TextAnnotation ta : annotations2add) {
			ta.addToIndexes();
		}

		for (Annotation annot : annotations2remove) {
			annot.removeFromIndexes();
		}
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ConceptMapper2CCPTypeSystemConverter_AE.class, tsd);
	}


	public TextAnnotation convertOntologyTerm(IdDictTerm term, JCas jcas) 
	throws AnalysisEngineProcessException {

		String type = term.getDictCanon(); 
		String id = term.getId();

		TextAnnotation ta = new TextAnnotation(jcas);
		ta.setBegin(term.getBegin());
		ta.setEnd(term.getEnd());
		ClassMention cm = new ClassMention(jcas);
		cm.setMentionName(id);

		Annotator annotator = new Annotator(jcas);
		annotator.setAffiliation("UIMA Sandbox");
		annotator.setFirstName("ConceptMapper");
		annotator.setLastName("ConceptMapper");
		annotator.setAnnotatorId(999);
		ta.setAnnotator(annotator);

		StringSlotMention slot = new StringSlotMention(jcas);
		slot.setMentionName(CANONICAL_NAME);
		StringArray slotValues = new StringArray(jcas,1);
		slotValues.set(0,type);
		slot.setSlotValues(slotValues);
		       
		FSArray slotMentions = new FSArray(jcas,1);
		slotMentions.set(0, slot);
		cm.setSlotMentions(slotMentions);
		       
		ta.setClassMention(cm);
		cm.setTextAnnotation(ta);

		FSArray spans = new FSArray(jcas, 1);
		Span span = new Span(jcas);
		span.setSpanStart(term.getBegin());
		span.setSpanEnd(term.getEnd());
		spans.set(0, span);
		ta.setSpans(spans);

		return ta;
	}

	public TextAnnotation convertToken(uima.tt.TokenAnnotation token, JCas jcas, int tokenNumber)
	throws AnalysisEngineProcessException {

		TextAnnotation ta = new TextAnnotation(jcas);
		ta.setBegin(token.getBegin());
		ta.setEnd(token.getEnd());

		ClassMention cm = new ClassMention(jcas);
		cm.setMentionName(TOKEN);

		IntegerSlotMention sm = new IntegerSlotMention(jcas);
		sm.setMentionName(TOKEN_NUMBER);
		IntegerArray slotValues = new IntegerArray(jcas, 1);
		slotValues.set(0, tokenNumber);
		sm.setSlotValues(slotValues);

		FSArray slots = new FSArray(jcas, 1);
		slots.set(0, sm);
		cm.setSlotMentions(slots);
		ta.setClassMention(cm);

		ta.setClassMention(cm);
		cm.setTextAnnotation(ta);

		FSArray spans = new FSArray(jcas, 1);
		Span span = new Span(jcas);
		span.setSpanStart(token.getBegin());
		span.setSpanEnd(token.getEnd());
		spans.set(0, span);
		ta.setSpans(spans);

		Annotator annotator = new Annotator(jcas);
		annotator.setAffiliation("UIMA Sandbox");
		annotator.setFirstName("ConceptMapper");
		annotator.setLastName("Tokenizer");
		annotator.setAnnotatorId(990);
		ta.setAnnotator(annotator);

		return ta;
	}

}
