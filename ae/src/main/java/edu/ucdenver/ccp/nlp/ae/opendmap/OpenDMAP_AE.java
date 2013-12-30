/*
 * OpenDMAP_AE.java
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

package edu.ucdenver.ccp.nlp.ae.opendmap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import org.uimafit.factory.AnalysisEngineFactory;

import edu.uchsc.ccp.opendmap.DMAPItem;
import edu.uchsc.ccp.opendmap.DMAPToken;
import edu.uchsc.ccp.opendmap.InfoPacket;
import edu.uchsc.ccp.opendmap.Parser;
import edu.uchsc.ccp.opendmap.Pattern;
import edu.uchsc.ccp.opendmap.ProtegeFrameItem;
import edu.uchsc.ccp.opendmap.Reference;
import edu.uchsc.ccp.opendmap.merging.TemplateMerger;

import edu.ucdenver.ccp.nlp.ts.Annotator;
import edu.ucdenver.ccp.nlp.ts.DocumentInformation;
import edu.ucdenver.ccp.nlp.ts.Span;
import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
//import edu.ucdenver.ccp.nlp.ts.AnnotationMetadata;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.ts.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;
import edu.ucdenver.ccp.nlp.ts.SlotMention;

//import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/////import edu.ucdenver.ccp.nlp.wrapper.opendmap.OpenDMAPPatternProperty;
import edu.ucdenver.ccp.nlp.ae.opendmap.AnnotationMap;
import edu.ucdenver.ccp.nlp.ae.opendmap.DMAPJcasDependencyConstraintHandler;
import edu.ucdenver.ccp.nlp.ae.opendmap.DMAPJcasTokenizer;
import edu.ucdenver.ccp.nlp.ae.opendmap.OpenDMAPRuntimeException;
import edu.ucdenver.ccp.nlp.ae.opendmap.ReferenceProcessor;

/**
 * This class packages OpenDMAP into the UIMA framework.
 * <p>
 * This annotator assumes that the input GeneRIF can be found in the JCas in TextAnnotation structures and copies the
 * output references of interest into the JCas as TextAnntotion structures.
 * 
 * @author R. James Firby
 */
public class OpenDMAP_AE extends OpenDmapAnnotator {

	private static Logger logger = Logger.getLogger(OpenDMAP_AE.class);

	/* These are the parameter names exposed in the UIMA dscriptor for this annotator */
	public static final String PARAM_IGNORE_DEPENDENCY_CONSTRAINTS = "IgnorePhraseConstraints";

	public static final String PARAM_DEBUG = "PrintDebugInformation";

	public static final String PARAM_CONTEXTFILE = "PipelineContextFile";

	public static final String PARAM_SPAN = "SpanFeatureStructure";

	public static final String PARAM_TEMPLATE_MERGE = "TemplateMerging";

	public static final String PARAM_KEEP_SUBSUMED_MATCHES = "KeepSubsumedMatches";

	/* The default setting for the 'Print Debug Information' parameter */
	private boolean debug = false;

	/* The deault value for the 'Ignore Phrase Constraints' parameter */
	private boolean ignorePhraseConstraints = false;

	/* The Annotation to delimit DMAP parsing (SENTENCES!) */
	String spanAnnotationTypeName = null;
	private Type spanAnnotationType = null;

	private boolean keepSubsumed = false;

	private boolean runTemplateMerging = false;
	TemplateMerger templateMerger = null;

	/*
	 * The annotation map that maps from input JCas annotations to DMAP tokens and from DMAP output references back to
	 * JCas annotations.
	 */
	private AnnotationMap annotationMap = null;

	/**
	 * Create a new GeneRIF Annotator that uses OpenDMAP to process the input document text.
	 */
	public OpenDMAP_AE() {
		super();
	}

	/**
	 * Initialize the OpenDMAP Annotator. Get the parameter values, load the annotation map, and create the parser.
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		// See if we should ignore phrase constraints
		ignorePhraseConstraints = false;
		Boolean param = null;
		param = (Boolean) context.getConfigParameterValue(PARAM_IGNORE_DEPENDENCY_CONSTRAINTS);
		if (param != null) {
			ignorePhraseConstraints = param.booleanValue();
		}

		param = (Boolean) context.getConfigParameterValue(PARAM_TEMPLATE_MERGE);
		if (param != null) {
			runTemplateMerging = param.booleanValue();
		}

		// See if we should print debugging information
		debug = false;
		param = null;
		param = (Boolean) context.getConfigParameterValue(PARAM_DEBUG);
		if (param != null) {
			debug = param.booleanValue();
		}

		// See if we should keep subsumed references of interest
		param = null;
		param = (Boolean) context.getConfigParameterValue(PARAM_KEEP_SUBSUMED_MATCHES);
		if (param != null) {
			keepSubsumed = param.booleanValue();
		}

		// Setup input and output mapping information
		String contextFile = null;
		contextFile = (String) context.getConfigParameterValue(PARAM_CONTEXTFILE);
		if (contextFile == null) {
			annotationMap = new AnnotationMap();
		} else {
			try {
				annotationMap = new AnnotationMap(contextFile);
			} catch (Exception e) {
				throw new ResourceInitializationException(new RuntimeException("Cannot load OpenDMAP_AE context file '" + new File(contextFile) + "'",
						 e));
						// wtf? new Object[] { this }, (Throwable) e);
			}
		}

		spanAnnotationTypeName = (String) context.getConfigParameterValue(PARAM_SPAN);

		// Initialize the parser
		super.initialize(context);
	}

	/**
	 * Perform local type system initialization.
	 * 
	 * @param aTypeSystem
	 *            the current type system.
	 * @see org.apache.uima.analysis_engine.annotator.TextAnnotator#typeSystemInit(TypeSystem)
	 */
	public void typeSystemInit(TypeSystem typeSystem) throws AnnotatorConfigurationException, AnnotatorInitializationException {
		// Get Annotation which spans search, if any.
		if (spanAnnotationTypeName != null) {
			spanAnnotationType = typeSystem.getType(spanAnnotationTypeName);
			Type ccptaType = typeSystem.getType("edu.ucdenver.ccp.nlp.ts.TextAnnotation");
			if (spanAnnotationType != null && !typeSystem.subsumes(ccptaType, spanAnnotationType)) {
				throw new AnnotatorConfigurationException();
			}
		}
	}

	/**
	 * Process the JCas using the annotation map to help extract input tokens. Write the references found back into the
	 * JCas using the annotation map to describe the desired output annotations.
	 */
	@Override
	public void process(Parser parser, JCas jcas) {
		/*
		 * checking for an empty document.. skip processing if there is no document text (instead of failing because
		 * there are no input tokens)
		 */
		String documentText = jcas.getDocumentText();
		if (documentText.trim().length() > 0) {

			ArrayList<TextAnnotation> annotationsToAdd = new ArrayList<TextAnnotation>();
			ArrayList<ClassMention> mentionsToAdd = new ArrayList<ClassMention>();

			if (templateMerger == null && runTemplateMerging) {
				templateMerger = new TemplateMerger(parser.getProtegeProjectGroup());
			}

			// A debugging heading
			if (debug) {
				FSIterator docInfoIter = jcas.getJFSIndexRepository().getAnnotationIndex(DocumentInformation.type).iterator();
				String docID = "-1";
				if (docInfoIter.hasNext()) {
					DocumentInformation docInfo = (DocumentInformation) docInfoIter.next();
					docID = docInfo.getDocumentId();
				}
				System.out.println("---------------------------------------------------------------------------");
				System.out.println(docID + ": " + jcas.getDocumentText());
				System.out.println();
				System.out.println("Annotation Context Map");
				if (annotationMap != null)
					annotationMap.print(System.out, 2);
			}

			if (spanAnnotationType == null) {

				System.out.println("OpenDMAP_AE.process - NOT using spanAnnotationType");
				// Get the input tokens from the JCas
				DMAPJcasTokenizer tokenizer = null;
				try {
					tokenizer = new DMAPJcasTokenizer(parser, jcas, annotationMap, debug);
				} catch (OpenDMAPRuntimeException odre) {
					System.err.println(odre.getMessage());
					System.exit(-1);
				}
				// Get the Stanford Parser Phrase Annotations as a graph
				DMAPJcasDependencyConstraintHandler tree = null;
				if (!ignorePhraseConstraints) {
					tree = new DMAPJcasDependencyConstraintHandler(jcas, debug);
				}
				if (debug) {
					if (tree != null) {
						System.out.println("\nStanford Parser Phrase Graph:");
						tree.print(System.out);
					}
					System.out.flush();
				}
				// Reset the parser
				parser.reset();
				// Parse the utterance
				parser.parse(tokenizer, tree);
				// Extract matches after each parse
				extractMatches(parser, jcas, annotationsToAdd, mentionsToAdd);
			} else {
				//System.out.println("OpenDMAP_AE.process - using spanAnnotationType");
				// Get the input tokens from the JCas
				try {
					DMAPJcasTokenizer tokenizer = new DMAPJcasTokenizer(parser, jcas, annotationMap, debug);

					// Get the Stanford Parser Phrase Annotations as a graph
					DMAPJcasDependencyConstraintHandler tree = null;
					if (!ignorePhraseConstraints) {
						tree = new DMAPJcasDependencyConstraintHandler(jcas, debug);
					}
					if (debug) {
						if (tree != null) {
							System.out.println("\nStanford Parser Phrase Graph:");
							tree.print(System.out);
						}
						System.out.flush();
					}

					FSIterator spanIterator = jcas.getAnnotationIndex(spanAnnotationType).iterator();

					// For each spanning annotation, parse.
					while (spanIterator.hasNext()) {
						TextAnnotation spanAnnotation = (TextAnnotation) spanIterator.next();
						tokenizer.constrainIterator(spanAnnotation);

						// Reset the parser
						parser.reset();
						// Parse the utterance
						parser.parse(tokenizer, tree);
						// Extract matches after each parse
						extractMatches(parser, jcas, annotationsToAdd, mentionsToAdd);
					}
				} catch (OpenDMAPRuntimeException odre) {
					System.err.println(odre.getMessage());
					System.exit(-1);
				}
			}

			for (ClassMention mention : mentionsToAdd) {
				mention.addToIndexes();
			}
			for (TextAnnotation annotation : annotationsToAdd) {
				annotation.addToIndexes();
			}
		} else {
			// TODO resurrect getDocumentID
			logger.warn("Empty document detected by OpenDMAP: " /*+ UIMA_Util.getDocumentID(jcas) */);
		}
	}

	protected void extractMatches(Parser parser, JCas jcas, ArrayList<TextAnnotation> annotationsToAdd,
			ArrayList<ClassMention> mentionsToAdd) {
		// Extract the best matches

		// System.out.println("PRINTING OUT ALL REFERENCES----------------------------");
		// for (Reference ref : parser.getReferences()) {
		// 	System.out.println("Ref: " + ref.getReferenceString());
		// }

		Collection<Reference> references 
			= ReferenceProcessor.getRecognizedAllReferences(parser, annotationMap.getReferencesOfInterest(),
				keepSubsumed, debug);

		if (debug) {
		    logger.debug("PRINTING OUT ALL REFERENCES----------------------------");
		    for (Reference ref : parser.getReferences()) {
		        logger.debug("Ref: " + ref.getReferenceString());
		    }
		    
		    logger.debug("PRINTINT OUT ALL UNSUBSUMED REFERENCES------------------");
		    for (Reference ref : parser.getUnsubsumedReferences()) {
		        logger.debug("Unsubsumed Ref: " + ref.getReferenceString());
		    }
		}

		if (false) {
			logger.debug("CHECKING SUBSUMPTIONS-----------------------------------");
			for (Reference ref : parser.getReferences()) {
				logger.debug("CHECKING: " + ref.getReferenceString());
				for (Reference sref : parser.getReferences()) {
					if (sref.subsumes(ref)) {
						logger.debug("Ref: " + ref.getReferenceString() + "\tSubsumed by: " + sref.getReferenceString());
					}
				}
			}
		}

		// Write them back to the JCas
		ArrayList<Reference> seen = new ArrayList<Reference>();
		//logger.debug("Writing references back to the CAS: " + references.size());
		for (Reference ref : references) {
			// Keep only the first (highest scoring) reference for each root
			//System.out.println("Checking reference: [" + ref.getCharacterStart() + ".."+ ref.getCharacterEnd() +"] "+ ref.getText());
			if (!isSeenReference(seen, ref, annotationMap)) {

				// Attempt to fill empty slots from the context, and add to the context
				if (runTemplateMerging)
					templateMerger.mergeReference(ref);

				//logger.debug("Creating annotation for ref" + "[" + ref.getStart() + ".." + ref.getEnd() + "]" + ": "
				//		+ ref.getReferenceString());
				// Create the skeleton annotation
				TextAnnotation annotation = annotationFromReference(ref, jcas, annotationMap);
				// Generate the class mention associated with this annotation
				ClassMention mention = mentionFromReference(ref, jcas, annotationMap);
				// Link the annotation and reference
				linkAnnotationToMention(annotation, mention, jcas);

				/* Add the pattern that was used to match this reference to the output annotation */
				//OpenDMAPPatternProperty patternProperty = new OpenDMAPPatternProperty(jcas);
				Pattern pattern = parser.getPatternUsedToMatchReference(ref);
				if (pattern != null) {
					//logger.debug("TRACED BACK PATTERN, ADDED TO METADATA: " + pattern.toString());
					//patternProperty.setPattern(pattern.toString());
					int patternID = -1;
					if (parser.getPattern2IDMap().containsKey(pattern.toString())) {
						patternID = parser.getPattern2IDMap().get(pattern.toString());
					} else {
						logger.error("Pattern expected, but not found in pattern2id map.");
					}
					//patternProperty.setPatternID(patternID);

					/*  *****  OLD-SCHOOL TODO FIX ******
					AnnotationMetadata metaData = new AnnotationMetadata(jcas);
					FSArray properties = new FSArray(jcas, 1);
					properties.set(0, patternProperty);
					metaData.setMetadataProperties(properties);
					annotation.setAnnotationMetadata(metaData);
					*/
				} else {
					logger.debug("ERROR -- null pattern traced back for annotation: ");// +
					// UIMA_Util.printTextAnnotation(annotation,
					// System.err);
				}

				annotationsToAdd.add(annotation);
				mentionsToAdd.add(mention);
				// This becomes a known root
				seen.add(ref);
			} 
			else if (false) {
				//System.out.println("IGNORING: [" + ref.getCharacterStart() + ".."+ ref.getCharacterEnd() +"] "+ ref.getText());
				if (debug) {
					logger.debug("SKIPPING Creating annotation for ref: " + ref.getReferenceString());
				}
			}
		}
	}

	/**
	 * Create the cross-links needed between a TextAnnotation and the ClassMention that was recognized for that
	 * annotation.
	 * 
	 * @param annotation
	 *            The text annotation..
	 * @param mention
	 *            The mention recognized using that annotation.
	 * @param jcas
	 *            The JCas holding the annotation and mention.
	 */
	protected void linkAnnotationToMention(TextAnnotation annotation, ClassMention mention, JCas jcas) {
		mention.setTextAnnotation(annotation);
		annotation.setClassMention(mention);
	}

	/**
	 * Get the list of text spans from the original text that were used to recognize a reference.
	 * 
	 * @param r
	 *            The reference from OpenDMAP.
	 * @param annotationMap
	 *            The annotation map describing the output annotations of interest.
	 * @param jcas
	 *            The JCas holding the original input text and annotations.
	 * @return A set of text spans including the text and the start and end character positions for each.
	 */
	protected ArrayList<Span> getSpanList(Reference r, AnnotationMap annotationMap, JCas jcas) {
		//logger.debug("GetSpanList for Reference: [" + r.getCharacterStart() + ".." + r.getCharacterEnd() + "]" + r.getReferenceString());
		ArrayList<Span> spans = new ArrayList<Span>();
		// Get the root reference
		Reference root = annotationMap.getOutputReferenceRoot(r);
		//logger.debug("-----Root Reference: [" + root.getCharacterStart() + ".." + root.getCharacterEnd() + "]" + root.getReferenceString());
		// if (r.isa("c-bioprocess") && r.hasSlotValue("action")) {
		if (r != root) {
			//logger.debug("----- r != root");
			// Treat this sort of reference special
			// Reference action = r.getSlotValue("action");

			// addTokenSpans(spans, root, null, jcas);
			/*
			 * Previously null was submitted for the AnnotationRoot when r!=root. This prevents recursive calls to check
			 * to see if slot fillers have root=true. The null, however was used by addTokenSpans as a flag, so we have
			 * added a flag to addTokenSpans to reflect where r==root or r!=root. This is probably not the most elegant
			 * way to do this, but should work for now.
			 */
			addTokenSpans(spans, root, annotationMap, jcas, false);
		} else {
			// A generic span generator
			//logger.debug("----- r == root");

			addTokenSpans(spans, root, annotationMap, jcas, true);
		}
		// Return the spans
		return spans;
	}

	/**
	 * Combine input tokens in continuous spans for use in constructing the output annotations.
	 * 
	 * @param spans
	 *            The span list being added to.
	 * @param reference
	 *            The reference that was recognized.
	 * @param annotationMap
	 *            The annotation map identifying output classes of interest.
	 * @param jcas
	 *            The JCas holding the input and output annotations.
	 */
	/*
	 * Previously null was submitted for the AnnotationRoot when r!=root in getSpanList() [see above]. This prevents
	 * recursive calls to check to see if slot fillers have root=true. The null, however was used by addTokenSpans as a
	 * flag, so we have added a flag to addTokenSpans to reflect where r==root or r!=root. This is probably not the most
	 * elegant way to do this, but should work for now.
	 */
	private void addTokenSpans(ArrayList<Span> spans, Reference reference, AnnotationMap annotationMap, JCas jcas, boolean isGenericSpan) {
		Span current = null;
		int next = 0;

		if (isGenericSpan) {
			for (DMAPToken token : reference.getTokens()) {
				if (!tokenUsedInSlot(token, reference, annotationMap)) {
					//logger.debug("Adding token to span: [" + token.getCharacterStart() + ".." + token.getCharacterEnd() + "]");
					if (current == null) {
						// The very first span
						current = new Span(jcas);
						current.setSpanStart(token.getCharacterStart());
						current.setSpanEnd(token.getCharacterEnd());
						next = token.getEnd() + 1;
					} else if (token.getStart() == next) {
						// Extend this span
						current.setSpanEnd(token.getCharacterEnd());
						next = token.getEnd() + 1;
					} else {
						// Need to start a new span
						spans.add(current);
						current = new Span(jcas);
						current.setSpanStart(token.getCharacterStart());
						current.setSpanEnd(token.getCharacterEnd());
						next = token.getEnd() + 1;
					}
				}
			}
		}

		/*
		 * If this is not a "generic span" (whatever that means?!) then we want to see if the root for this reference is
		 * the reference itself. If it is not, then return the tokens for the root of this reference. If it is, then
		 * return the tokens for the reference itself.
		 */
		Reference root = annotationMap.getOutputReferenceRoot(reference);
		if (reference != root) {
			addTokenSpans(spans, root, annotationMap, jcas, false);
		} else {

			for (DMAPToken token : reference.getTokens()) {
				//logger.debug("Adding token to span: [" + token.getCharacterStart() + ".." + token.getCharacterEnd() + "]");
				if (current == null) {
					// The very first span
					current = new Span(jcas);
					current.setSpanStart(token.getCharacterStart());
					current.setSpanEnd(token.getCharacterEnd());
					next = token.getEnd() + 1;
				} else if (token.getStart() == next) {
					// Extend this span
					current.setSpanEnd(token.getCharacterEnd());
					next = token.getEnd() + 1;
				} else {
					// Need to start a new span
					spans.add(current);
					current = new Span(jcas);
					current.setSpanStart(token.getCharacterStart());
					current.setSpanEnd(token.getCharacterEnd());
					next = token.getEnd() + 1;
				}
			}
		}
		if (current != null)
			spans.add(current);
	}

	/**
	 * Check whether a token was used to recognize a slot filler or just the enclosing reference.
	 * 
	 * @param token
	 *            The token to check.
	 * @param r
	 *            The enclosing reference.
	 * @param annotationMap
	 *            The annotation map giving the slots of interest.
	 * @return True if the token was used to recognize a slot filler of interest.
	 */
	private boolean tokenUsedInSlot(DMAPToken token, Reference r, AnnotationMap annotationMap) {
		if (r.getInformation() == null)
			return false;
		for (InfoPacket pair : r.getInformation()) {
			Reference value = pair.getValue();
			if (annotationMap.isOutputReferenceOfInterest(value)) {
				for (DMAPToken tok : value.getTokens()) {
					if (tok == token)
						return true;
				}
			} else {
				if (tokenUsedInSlot(token, value, annotationMap))
					return true;
			}
		}
		return false;
	}

	/**
	 * Check whether this type of reference has already been put into the JCas. Only the instance of a reference with
	 * the highest score is output.
	 * 
	 * @param seen
	 *            A list of references that have already been output.
	 * @param ref
	 *            The reference to check.
	 * @param annotationMap
	 *            The annotation map holding output classes of interest.
	 * @return True if the reference being checked has already been output.
	 */
	protected boolean isSeenReference(ArrayList<Reference> seen, Reference ref, AnnotationMap annotationMap) {
		Reference root = annotationMap.getOutputReferenceRoot(ref);
		double refScore = ReferenceProcessor.calculateScore(ref);
		//System.out.println("Root reference: " + root.getReferenceString());
		for (Reference old : seen) {
			// System.out.println("Root: " + root + " - " + ref);
			// if (annotationMap.getOutputReferenceRoot(old) == root) {
			Reference oldRootReference = annotationMap.getOutputReferenceRoot(old);
			if (oldRootReference.equals(root)) {
				if (oldRootReference.getCharacterStart() == root.getCharacterStart() 
					&& oldRootReference.getCharacterEnd() == root.getCharacterEnd() 
					&& ReferenceProcessor.calculateScore(old) >= refScore) {
					//logger.debug("Root has been put into the CAS already, isSeenReference = true");
					return true;
				}
			} else {
				//Reference oldRoot = annotationMap.getOutputReferenceRoot(old);
				//logger.debug("Root of old does not match, " + oldRoot.getReferenceString());
			}
		}
		//logger.debug("isSeenReference = false");
		return false;
	}

	/**
	 * Generate a JCas TextAnnotation from an OpenDMAP output reference.
	 * 
	 * @param r
	 *            The reference to write to the JCas.
	 * @param jcas
	 *            The JCas to hold the annotation for this reference.
	 * @param annotationMap
	 *            The annotation map describing slot mappings.
	 * @return The new TextAnnotation corresponding to the reference.
	 */
	protected TextAnnotation annotationFromReference(Reference r, JCas jcas, AnnotationMap annotationMap) {
		TextAnnotation annotation = new TextAnnotation(jcas);
		// Generate the annotator information for this annotation
		Annotator annotator = new Annotator(jcas);
		annotator.setAnnotatorId(15); // ?
		annotator.setFirstName("OpenDMAP");
		annotator.setLastName("GenerifAnnotator");
		annotator.addToIndexes();
		annotation.setAnnotator(annotator);
		// Generate all the span information
		List<Span> spans = getSpanList(r, annotationMap, jcas);
		if (spans.isEmpty()) {
			Span span = new Span(jcas);
			span.setSpanStart(r.getCharacterStart());
			span.setSpanEnd(r.getCharacterEnd());
			spans.add(span);
		}
		FSArray casSpans = new FSArray(jcas, spans.size());
		for (int i = 0; i < spans.size(); i++) {
			casSpans.set(i, spans.get(i));
		}
		annotation.setSpans(casSpans);
		annotation.setBegin(spans.get(0).getSpanStart());
		annotation.setEnd(spans.get(spans.size() - 1).getSpanEnd());
		// Return the skeleton annotation

		//logger.debug("REFERENCE SPANS: " + r.getCharacterStart() + ".." + r.getCharacterEnd());

		return annotation;
	}

	/**
	 * Create a ClassMention for an OpenDMAP output reference.
	 * 
	 * @param r
	 *            The output reference.
	 * @param jcas
	 *            The JCas to hold the new class mention.
	 * @param annotationMap
	 *            The annotation map holding slot name translations.
	 * @return The new ClassMention for the reference.
	 */
	protected ClassMention mentionFromReference(Reference r, JCas jcas, AnnotationMap annotationMap) {
		// See if we have a cached mention that covers us already
		List<DMAPToken> tokens = r.getTokens();
		if (tokens != null) {
			// Look for a single cached annotation
			TextAnnotation cachedAnnotation = null;
			List<InfoPacket> cachedSlots = null;
			for (DMAPToken token : tokens) {
				Object source = token.getSource();
				if ((source != null) && (source instanceof TextAnnotation)) {
					if (cachedAnnotation == null) {
						// No cached one yet
						cachedAnnotation = (TextAnnotation) source;
						cachedSlots = token.getInformation();
					} else {
						// Multiple cached annoations, we need to build a new one
						cachedAnnotation = null;
						cachedSlots = null;
						break;
					}
				}
			}
			if (cachedAnnotation != null) {
				// Okay, this reference arose from a single external TextAnnotation
				// See if we have added any information to it
				List<InfoPacket> pairs = r.getInformation();
				if ((pairs == null) && (cachedSlots == null)) {
					// No new information
					return cachedAnnotation.getClassMention();
				} else if ((pairs != null) && (cachedSlots != null) && (pairs.size() == cachedSlots.size())) {
					boolean match = true;
					for (InfoPacket a : pairs) {
						boolean found = false;
						for (InfoPacket b : cachedSlots) {
							if (a.getKey().equals(b.getKey())) {
								found = true;
								break;
							}
						}
						if (!found) {
							match = false;
							break;
						}
					}
					if (match) {
						for (InfoPacket a : cachedSlots) {
							boolean found = false;
							for (InfoPacket b : pairs) {
								if (a.getKey().equals(b.getKey())) {
									found = true;
									break;
								}
							}
							if (!found) {
								match = false;
								break;
							}
						}
					}
					// No new information, return the cached mention
					if (match)
						return cachedAnnotation.getClassMention();
				}
			}
		}
		// Okay, not a cached mention, create a new one
		ClassMention mention = new ClassMention(jcas);
		mention.setMentionName(annotationMap.getOutputReferenceMentionType(r));
		List<InfoPacket> slotInfo = r.getInformation();
		if ((slotInfo != null) && !slotInfo.isEmpty()) {
			ArrayList<SlotMention> slots = new ArrayList<SlotMention>();
			for (int i = 0; i < slotInfo.size(); i++) {
				InfoPacket pair = slotInfo.get(i);
				if (annotationMap.isOutputReferenceSlotOfInterest(r, pair.getKey())) {
					DMAPItem value = pair.getValue().getItem();
					if (value instanceof ProtegeFrameItem) {
						ComplexSlotMention slot = new ComplexSlotMention(jcas);
						slot.setMentionName(annotationMap.getOutputReferenceSlotMentionType(r, pair.getKey()));
						ClassMention slotFiller = mentionFromReference(pair.getValue(), jcas, annotationMap);
						TextAnnotation annotation = annotationFromReference(pair.getValue(), jcas, annotationMap);
						linkAnnotationToMention(annotation, slotFiller, jcas);
						FSArray values = new FSArray(jcas, 1);
						values.set(0, slotFiller);
						slot.setClassMentions(values);
						slots.add(slot);
					} else {
						StringSlotMention slot = new StringSlotMention(jcas);
						slot.setMentionName(annotationMap.getOutputReferenceSlotMentionType(r, pair.getKey()));
						StringArray values = new StringArray(jcas, 1);
						values.set(0, value.getText());
						slot.setSlotValues(values);
						slots.add(slot);
					}
				}
			}
			if (!slots.isEmpty()) {
				FSArray jcasSlots = new FSArray(jcas, slots.size());
				for (int i = 0; i < slots.size(); i++)
					jcasSlots.set(i, slots.get(i));
				mention.setSlotMentions(jcasSlots);
			}
		}
		// TODO If all is well, there should have been an annotation built somewhere
		// for this mention. We should look it up somehow and stick it into the mention.
		// TextAnnotation annotation = annotationFromReference(r, jcas, interests);
		// linkAnnotationToMention(annotation, mention, jcas);
		// annotation.addToIndexes();
		return mention;
	}

//	protected void debug(String message) {
//		if (DEBUG) {
//			System.err.println("DEBUG -- OpenDMAP_AE: " + message);
//		}
//	}
//
//	protected void error(String message) {
//		System.err.println("ERROR -- OpenDMAP_AE: " + message);
//	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @return the keepSubsumed
	 */
	public boolean isKeepSubsumed() {
		return keepSubsumed;
	}

	/**
	 * @return the annotationMap
	 */
	public AnnotationMap getAnnotationMap() {
		return annotationMap;
	}

	/**
	 * @return the ignorePhraseConstraints
	 */
	public boolean isIgnorePhraseConstraints() {
		return ignorePhraseConstraints;
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(
		TypeSystemDescription tsd,
		boolean ignoreDependencyConstraints,
		boolean debug,
		String contextFileName,
		String configFileName,
		String spanName,
		boolean templateMerge,
		boolean keepSubsumedMatches)		
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(
			OpenDMAP_AE.class, tsd,
			PARAM_IGNORE_DEPENDENCY_CONSTRAINTS, ignoreDependencyConstraints,
			PARAM_DEBUG, debug,
			PARAM_CONTEXTFILE, contextFileName,
			PARAM_CONFIGURATION_FILE, configFileName,
			PARAM_SPAN, spanName, 
			PARAM_TEMPLATE_MERGE, templateMerge,
			PARAM_KEEP_SUBSUMED_MATCHES, keepSubsumedMatches);
	}
}
