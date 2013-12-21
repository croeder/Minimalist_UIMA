/*
 * DMAPJcasTokenizer.java
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import edu.uchsc.ccp.opendmap.DMAPToken;
import edu.uchsc.ccp.opendmap.DMAPTokenizer;
import edu.uchsc.ccp.opendmap.Parser;
import edu.uchsc.ccp.opendmap.Reference;

import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.tsx.ClassMentionX;
import edu.ucdenver.ccp.nlp.tsx.SlotMentionX;
import edu.ucdenver.ccp.nlp.ts.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.ts.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;
import edu.ucdenver.ccp.nlp.ts.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.ts.DoubleSlotMention;
import edu.ucdenver.ccp.nlp.ts.FloatSlotMention;
import edu.ucdenver.ccp.nlp.ts.BooleanSlotMention;

/**
 * Helper class to get all of the appropriate annotation structures
 * out of the JCas and return them as DMAPToken structures
 * for the OpenDMAP parser.
 * 
 * There are two types of annotations of interest at the moment:
 * <ul>
 * <li> TokenAnnotation structures that correspond to individual words and pieces of punctuation
 * <li> ClassMentionAnnotation structures that correspond to "named entities"
 * </ul>
 * 
 * @author R. James Firby
 */
public class DMAPJcasTokenizer extends DMAPTokenizer {
	
	/* The CAS iterator of the TokenAnnotation structures */
	private ArrayList<DMAPToken> tokens = new ArrayList<DMAPToken>();
	
	private Iterator<DMAPToken> tokenIterator = null;
	
	/**
	 * Create a new iterator over structures in the JCas.
	 * 
	 * @param parser The parser that will be using this tokenizer
	 * @param jcas The JCas holding the annotations to take as input
	 * @param annotationMap The map from JCas annotations to words and DMAP Protege Frames
	 * @param debug Print out additional debugging information if true 
	 */
	public DMAPJcasTokenizer(Parser parser, JCas jcas, AnnotationMap annotationMap, boolean debug) 
	throws OpenDMAPRuntimeException {
		super();
		JFSIndexRepository ir = jcas.getJFSIndexRepository();
		FSIndex index = ir.getAnnotationIndex(TextAnnotation.type);
		if (index != null) {
		    if (index.size() == 0) {
		        throw new OpenDMAPRuntimeException("Error. There are no TokenAnnotations"
					+ " in the CAS. OpenDMAP cannont function without them. Exiting...");
		    }
			FSIterator iterator = index.iterator();
			Set<Integer> tokenNumbers = new HashSet<Integer>();
			while (iterator.hasNext()) {
				// if this doesn't cast, something is really wrong. I'll take the runtime exception.
				TextAnnotation jcasToken = (TextAnnotation) iterator.next();
				ClassMention cm = jcasToken.getClassMention();
				if (cm.getMentionName().equals("token")) {
					
					DMAPToken token = DMAPToken.newToken(parser, 
						jcasToken.getCoveredText().toLowerCase(),
					 	jcasToken.getBegin(), jcasToken.getEnd());

					//Integer tokenNumber = jcasToken.getTokenNumber();
					//IntegerSlotMention tokenNumberSlot = (IntegerSlotMention) UIMA_Util.getPrimitiveSlotMentionByName(cm, "tokenNumber");
					IntegerSlotMention tokenNumberSlot = (IntegerSlotMention) ClassMentionX.getPrimitiveSlotMentionByName(cm, "tokenNumber");
					Integer  tokenNumber = tokenNumberSlot.getSlotValues(0);
					if (!tokenNumbers.contains(tokenNumber)) {
					    tokenNumbers.add(tokenNumber);
					} 
					else {
						if (tokenNumber == null) {
					    	throw new OpenDMAPRuntimeException("Error. An issue with"
								+ " token numbering has been detected. Token number"
								+ " is null for span: "
								+ "[" + jcasToken.getBegin() 
								+ ".." + jcasToken.getEnd() + "] \""
								+ jcasToken.getCoveredText() + "\"."
								+ " OpenDMAP cannot function properly without token"
								+ " numbers for each token. Exiting...");
						} 
						else {
							throw new OpenDMAPRuntimeException(
								"Error. An issue with token numbering has been "
							+ "detected. A duplicate token number has been found ("
							+tokenNumber+") span: [" + jcasToken.getBegin() + ".."
							+ jcasToken.getEnd() + "] \""
							+ jcasToken.getCoveredText() 
							+ "\". OpenDMAP cannot function properly without "
							+ "token numbers for each token. Exiting...");
						}
					}
					token.setStart(tokenNumber);
					token.setEnd(tokenNumber);
					sortInNewToken(token);
				}
				
			}
		} else {
		    throw new OpenDMAPRuntimeException(
				"Error. There are no TokenAnnotations in the CAS. "
				+ "OpenDMAP cannont function without them. Exiting...");
		}
		// Grab out any desired ClassMentionAnnotations.
		// These correspond to gene names, molecules and other named entities.
		if ((annotationMap != null) && (annotationMap.hasInputMentionsOfInterest())) {
			String docString = null;
			if (debug) {
				System.out.println("\nPre-Annotated Mentions:");
				docString = jcas.getDocumentText();
			}
			index = ir.getAnnotationIndex(TextAnnotation.type);
			if (index != null) {
				ArrayList<DMAPToken> semanticTokens = new ArrayList<DMAPToken>();
				FSIterator iterator = index.iterator();
				while (iterator.hasNext()) {
					Object thing = iterator.next();
					if (thing instanceof TextAnnotation) {
						TextAnnotation jcasToken = (TextAnnotation) thing;
						DMAPToken token = buildDMAPToken(parser, jcasToken, annotationMap);
						if (token != null) {
							token.setSource(jcasToken);
							if (token != null) {
								int start = 0;
								for (int i=0; i<tokens.size(); i++) {
									if (token.getCharacterStart() < tokens.get(i).getCharacterEnd()) {
										start = i;
										break;
									}
								}
								int end = tokens.size()-1;
								for (int i=0; i<tokens.size(); i++) {
									if (token.getCharacterEnd() <= tokens.get(i).getCharacterStart()) {
										end = i - 1;
										break;
									}
								}
								if ((start <= end) && (start >= 0)) {
									token.setStart(start);
									token.setEnd(end);
									semanticTokens.add(token);
								}
							}
							if (debug) {
								AnnotationString desc = new AnnotationString(jcasToken, null, null, docString);
								System.out.println(" " + token.getStart() + ".." + token.getEnd() + " " + desc.toString());
							}
						}
					}
				}
				// Keep the list of tokens sorted by their end point
				for (DMAPToken token: semanticTokens) {
					sortInNewToken(token);
				}
			}
		}
//		for (DMAPToken token: tokens) {
//			System.out.println("Token: " + token.getStart() + "-" + token.getEnd() + "," + token.getCharacterStart()+ "-" + token.getCharacterEnd() + " '" + token.getItem().getText() + "'");
//		}
		tokenIterator = tokens.iterator();
	}
	
	public void constrainIterator(TextAnnotation spanAnnotation) {
		constrainIterator(spanAnnotation.getBegin(), spanAnnotation.getEnd() );
	}
	
	public void constrainIterator(int spanBegin, int spanEnd ) {
		ArrayList<DMAPToken> subtokens = new ArrayList<DMAPToken>();
		
		for (DMAPToken token: tokens) {
			if ( token.getCharacterStart() < spanBegin) {
				continue;
			} else if ( token.getCharacterEnd() <= spanEnd) {
				subtokens.add(token);
			} else {
				break;
			}
		}
		
		tokenIterator = subtokens.iterator();
	}
	
	/**
	 * Build a DMAPToken representing a Protege Frame reference found in
	 * the JCas.
	 * 
	 * @param parser The parser that will be using the token
	 * @param jcasToken The JCas annotation that corresponds to this token
	 * @param annotationMap The configuration map from JCas annotations to DMAP frames
	 * @return
	 */
	private DMAPToken buildDMAPToken(Parser parser, TextAnnotation jcasToken, AnnotationMap annotationMap) {
		ClassMention mention = jcasToken.getClassMention();
		String type = mention.getMentionName();
		if (annotationMap.isInputMentionOfInterest(type)) {
			Vector<Object> slots = getMentionSlotValues(parser, mention, annotationMap);
			return DMAPToken.newToken(parser, annotationMap.getInputMentionReferenceType(type), 
				slots, jcasToken.getBegin(), jcasToken.getEnd(), jcasToken);
		}
		return null;
	}	
	
	/**
	 * Build a DMAP Reference for a JCas mention annotation.  The type of the Reference will
	 * corrspond to a Protege Frame and any mention slots will be turned into Reference slot/filler
	 * pairs.
	 * 
	 * @param parser The parser that will be using this reference
	 * @param mention The mention in the JCas this reference represents
	 * @param annotationMap The configuration map from JCas annotations to DMAP frames
	 * @return The reference
	 */
	private Reference buildMentionReference(Parser parser, ClassMention mention, AnnotationMap annotationMap) {
		String type = mention.getMentionName();
		if (annotationMap.isInputMentionOfInterest(type)) {
			String dmapFrameName = annotationMap.getInputMentionReferenceType(type);
			Vector<Object> dmapSlots = getMentionSlotValues(parser, mention, annotationMap);
			return parser.newReference(dmapFrameName, dmapSlots);
		} else {
			return null;
		}
	}

	/**
	 * Generate a list of DMAP slot/filler pairs for the slots in a
	 * JCas mention.
	 * 
	 * @param parser The parser that will be using the reference holding these slots
	 * @param mention The mention in the JCas that has the slots
	 * @param annotationMap The configuration map from JCas annotations to DMAP frames
	 * @return The list of slot/filler pairs
	 */
	private Vector<Object> getMentionSlotValues(Parser parser, ClassMention mention, AnnotationMap annotationMap) {
		// Get the concept name
		String mentionName = mention.getMentionName();
		String dmapFrameName = annotationMap.getInputMentionReferenceType(mentionName);
		// Look at each slot
		Vector<Object> dmapSlots = new Vector<Object>();
		FSArray slots = mention.getSlotMentions();
		if (slots != null) {
			for (int i=0; i<slots.size(); i++) {
				Object jcasSlotMention = slots.get(i);
				if (jcasSlotMention instanceof ComplexSlotMention) {
					// A slot that holds other mentions, need to create a nested Reference
					ComplexSlotMention slot = (ComplexSlotMention) jcasSlotMention;
					// Get the slot name and see if it should be included
					String slotName = slot.getMentionName();
					if ((slotName != null) && annotationMap.isInputMentionSlotIncluded(mentionName, slotName)) {
						// A requested slot, get the DMAP name
						String dmapSlotName = annotationMap.getInputMentionSlotReferenceType(mentionName, slotName);
						if (dmapSlotName != null) {
							FSArray slotClassMentions = slot.getClassMentions();
							for (int j=0; j<slotClassMentions.size(); j++) {
								Object slotClassMention = slotClassMentions.get(j);
								if (slotClassMention instanceof ClassMention) {
									ClassMention classMention = (ClassMention) slotClassMention;
									// Create a reference out of a slot filler class mention
									Reference reference = buildMentionReference(parser, classMention, annotationMap);
									// If we have a slot value, save it away
									if (reference != null) {
										dmapSlots.add(parser.newReferenceSlot(dmapFrameName, dmapSlotName, reference));
									}
								}
							}
						}
					}
				} 
				else {
					// assume dmap wants strings
					
					PrimitiveSlotMention primitiveSlot = (PrimitiveSlotMention) jcasSlotMention;
					String slotName = primitiveSlot.getMentionName();
					String slotValue = null;

					if ((slotName != null) && annotationMap.isInputMentionSlotIncluded(mentionName, slotName)) {
						String dmapSlotName = annotationMap.getInputMentionSlotReferenceType(mentionName, slotName);


						if (dmapSlotName != null) {
					
							if (jcasSlotMention instanceof StringSlotMention) {
								StringSlotMention slot = (StringSlotMention) jcasSlotMention;
								slotValue = "" + SlotMentionX.getFirstSlotValue(slot);
							}
							else if (jcasSlotMention instanceof BooleanSlotMention) {
								BooleanSlotMention slot = (BooleanSlotMention) jcasSlotMention;
								slotValue = "" + SlotMentionX.getFirstSlotValue(slot);
							}
							else if (jcasSlotMention instanceof DoubleSlotMention) {
								DoubleSlotMention slot = (DoubleSlotMention) jcasSlotMention;
								slotValue = "" + SlotMentionX.getFirstSlotValue(slot);
							}
							else if (jcasSlotMention instanceof FloatSlotMention) {
								FloatSlotMention slot = (FloatSlotMention) jcasSlotMention;
								slotValue = "" + SlotMentionX.getFirstSlotValue(slot);
							}
							else if (jcasSlotMention instanceof IntegerSlotMention) {
								IntegerSlotMention slot = (IntegerSlotMention) jcasSlotMention;
								slotValue = "" + SlotMentionX.getFirstSlotValue(slot);
							}

							if (slotValue != null) {
								dmapSlots.add(parser.newReferenceSlot(dmapFrameName, dmapSlotName, slotValue));
							}
						}
					}
				}
			}
		}
		return dmapSlots;		
	}
	
	/**
	 * Check if there are any more tokens to process
	 */
	@Override
	public boolean hasNext() {
		// Are there any DefaultToken structures left
		if (tokenIterator == null) {
			return false;
		} else {
			return tokenIterator.hasNext();
		}
	}

	/**
	 * Grab the next token and return it.  The tokens are in a
	 * list sorted by ending character (which roughly corresponds to
	 * input token order).
	 */
	@Override
	public DMAPToken next() {
		// Grab the next DefaultToken and turn it into a DMAP Token
		if ((tokenIterator != null) && (tokenIterator.hasNext())) {
			return tokenIterator.next();
		} else {
			return null;
		}
	}
	
	/**
	 * Sort a new token into the list of tokens this tokenizer will
	 * return.
	 * 
	 * @param token The token to sort in
	 */
	private void sortInNewToken(DMAPToken token) {
		boolean found = false;
		for (int i=0; i<tokens.size(); i++) {
			if (token.getCharacterEnd() < tokens.get(i).getCharacterEnd()) {
				tokens.add(i, token);
				found = true;
				break;
			}
		}
		if (!found) tokens.add(token);
	}
	
}
