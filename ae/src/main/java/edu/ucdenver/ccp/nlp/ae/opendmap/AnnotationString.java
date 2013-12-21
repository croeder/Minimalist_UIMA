/*
 * AnnotationString.java
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

import org.apache.uima.jcas.cas.FSArray;

import edu.ucdenver.ccp.nlp.ts.Span;
import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.ts.ComplexSlotMention;

/**
 * This class generates a string describing a JCas TextAnnotation in a standard
 * format that can be used for generating and executing regression tests.
 * 
 * @author R. James Firby
 */
public class AnnotationString {
	
	// Cached version of the output string once created.
	private String myString = null;
	
	/**
	 * Create an object that will print as a standard format string for a JCas annotation.
	 * 
	 * @param annotation The annotation to describe in the string.
	 * @param slots The annotation slots to include.  Slots with names not in this list are excluded.
	 * @param docId The ID of the document associated with this annotation
	 * @param docText The document text associated with this annotatation
	 */
	public AnnotationString(TextAnnotation annotation, String slots[], String docId, String docText) {
		myString = create(annotation, slots, docId, docText);
	}
	
	/**
	 * Return the standard format string for this annotation.
	 */
	public String toString() {
		if (myString != null) return myString;
		return "";
	}
	
	/**
	 * Create a new standard format string for a JCas annotation.
	 * 
	 * @param annotation The annotation to describe in the string.
	 * @param slots The annotation slots to include.  Slots with names not in this list are excluded.
	 * @param docId The ID of the document associated with this annotation
	 * @param docText The document text associated with this annotatation
	 */
	public static String create(TextAnnotation annotation, String slotNames[], String docId, String docText) {
		// Pull the semantic mention out of the annotation
		ClassMention mention = annotation.getClassMention();
		String base = toTestString(getClassMentionBaseSpan(mention, docText), mention.getMentionName());
		// Begin creating the output string
		StringBuffer sb = new StringBuffer();
		if (docId != null) {
			sb.append(docId);
			sb.append("|");
		}
		sb.append(toSafeString(base));
		// Extract the slot fillers we care about
		if ((slotNames != null) && (slotNames.length > 0)) {
			String slotStrings[] = new String[slotNames.length];
			// Look at each slot
			FSArray slots = mention.getSlotMentions();
			if (slots != null) {
				for (int i=0; i<slots.size(); i++) {
					Object jcasSlotMention = slots.get(i);
					ComplexSlotMention slot = null;
					if (jcasSlotMention instanceof ComplexSlotMention) slot = (ComplexSlotMention) jcasSlotMention;
					if (slot != null) {
						String slotValue = null;
						FSArray slotClassMentions = slot.getClassMentions();
						for (int j=0; j<slotClassMentions.size(); j++) {
							Object slotClassMention = slotClassMentions.get(j);
							ClassMention classMention = null;
							if (slotClassMention instanceof ClassMention) classMention = (ClassMention) slotClassMention;
							if (classMention != null) {
								//System.out.println("Mention: " + classMention.getMentionName());
								TextSpan filler = getClassMentionSpan(classMention, docText);
								if (slotValue == null) {
									slotValue = toTestString(filler, classMention.getMentionName());
								} else {
									slotValue = slotValue + ";" + toTestString(filler, classMention.getMentionName());
								}
							}
						}
						if (slotValue != null) {
							String slotName = slot.getMentionName();
							if (slotName != null) {
								insertSlotString(slotNames, slotName, slotStrings, slotValue);
							}
						}
					}
				}
			}
			// Now add the slots of interest in order
			for (int i=0; i<slotStrings.length; i++) {
				sb.append("|");
				sb.append(toSafeString(slotStrings[i]));				
			}
		}
		// Done
		return sb.toString();
	}
	
	/**
	 * Place the value of slot into the corresponding array position as its slot name.
	 * 
	 * @param slotNames The names of the slots of interest
	 * @param slotName The name for the slot being processed
	 * @param slotStrings The values of the slots of interest
	 * @param slotValue The value of the slot being processed
	 */
	private static void insertSlotString(String slotNames[], String slotName, String slotStrings[], String slotValue) {
		for (int i=0; i<slotNames.length; i++) {
			if (slotName.equalsIgnoreCase(slotNames[i])) {
				slotStrings[i] = slotValue;
			}
		}
	}
	
	/**
	 * Generate a standard format string component for a text span from a particular source.
	 * 
	 * @param span The text span including the text and the start and end character positions
	 * @param source The source of this text span, often an external UIMA annotator
	 * @return The formatted string component
	 */
	private static String toTestString(TextSpan span, String source) {
		if (span == null) return "";
		if (source == null)
			return span.text + "[" + span.start + ".." + span.end + "]";
		return span.text + "[" + span.start + ".." + span.end + "]{" + source + "}";
	}
	
	private static String toSafeString(String string) {
		if (string == null) return "";
		return string;
	}

  /**
   * Create a string that includes all of the spans within this mention.
   * This includes recursively pulling out slot spans as well.
   * 
   * @param mention The mention to process
   * @param data The original document text
	 * @return A string summarizing all the text spans included in this mention
   */
	private static TextSpan getClassMentionSpan(ClassMention mention, String data) {
		// Create text spans for the base annotation and all the slots
		ArrayList<TextSpan> textSpans = new ArrayList<TextSpan>();
		addClassMentionSpans(textSpans, mention, data);
		// Assemble the sorted spans into a single string
		TextSpan result = appendSpans(textSpans);
		//result.source = mention.getMentionName();
		return result;
	}
	
	/**
	 * Create the 'base' span for this mention.  That is the spans not included in a slot.
	 * @param mention The mention to process
	 * @param data The original document text
	 * @return A string capturing the base spans for this mention
	 */
	private static TextSpan getClassMentionBaseSpan(ClassMention mention, String data) {
		// Create text spans for the base annotation only
		ArrayList<TextSpan> textSpans = new ArrayList<TextSpan>();
		addClassMentionBaseSpans(textSpans, mention, data);
		// Assemble the sorted spans into a single string
		return appendSpans(textSpans);
	}
	
	/**
	 * Add all of the text spans that take part in a mention into a sorted list of text spans.
	 * 
	 * @param textSpans The sorted list of text spans
	 * @param mention The mention from which spans are to be extracted
	 * @param data The original document text
	 * @return The sorted list of text spans
	 */
	private static ArrayList<TextSpan> addClassMentionSpans(ArrayList<TextSpan> textSpans, ClassMention mention, String data) {
		// Get the base annotation for this mention (it should point to a TextAnnotation)
		addClassMentionBaseSpans(textSpans, mention, data);
		// Now sort in all the spans from the mention slots
		FSArray slots = mention.getSlotMentions();
		if (slots != null) {
			for (int i=0; i<slots.size(); i++) {
				Object jcasSlotMention = slots.get(i);
				ComplexSlotMention slot = null;
				if (jcasSlotMention instanceof ComplexSlotMention) slot = (ComplexSlotMention) jcasSlotMention;
				if (slot != null) {
					FSArray slotClassMentions = slot.getClassMentions();
					for (int j=0; j<slotClassMentions.size(); j++) {
						Object slotClassMention = slotClassMentions.get(j);
						ClassMention classMention = null;
						if (slotClassMention instanceof ClassMention) classMention = (ClassMention) slotClassMention;
						if (classMention != null) {
							addClassMentionSpans(textSpans, classMention, data);
						}
					}
				}
			}
		}
		// Done
		return textSpans;
	}

	/**
	 * Add all of the text spans that are part of a base mention into a sorted list of text spans.
	 * 
	 * @param textSpans The sorted list of text spans
	 * @param mention The mention from which spans are to be extracted
	 * @param data The original document text
	 * @return The sorted list of text spans
	 */
	private static ArrayList<TextSpan> addClassMentionBaseSpans(ArrayList<TextSpan> textSpans, ClassMention mention, String data) {
		// Get the base annotation for this mention (it should point to a TextAnnotation)
		//FSArray annotations = mention.getCcpTextAnnotations();
		//if ((annotations == null) || (annotations.size() <= 0)) return textSpans;
		//Object jcasAnnotation = annotations.get(0);
		//TextAnnotation annotation = null
		//if (jcasAnnotation instanceof TextAnnotation) annotation = (TextAnnotation) jcasAnnotation;

		TextAnnotation annotation = mention.getTextAnnotation();
		if (annotation == null) return textSpans;
		// Get all the spans from the base annotation
		FSArray spans = annotation.getSpans();
		if (spans == null) {
			sortIn(textSpans, new TextSpan(annotation.getStart(), annotation.getEnd(), data.substring(annotation.getStart(), annotation.getEnd())));
		} else {
			for (int i=0; i<spans.size(); i++) {
				Object jcasSpan = spans.get(i);
				Span span = null;
				if (jcasSpan instanceof Span) span = (Span) jcasSpan;
				if (span != null)
					sortIn(textSpans, new TextSpan(span.getSpanStart(), span.getSpanEnd(), data.substring(span.getSpanStart(), span.getSpanEnd())));
			}
		}
		// Done
		return textSpans;
	}
	
	/**
	 * Append a list of spans together to create a single span.
	 * 
	 * @param textSpans The spans to append
	 * @return The resulting single span
	 */
	private static TextSpan appendSpans(ArrayList<TextSpan> textSpans) {
		StringBuffer sb = new StringBuffer();
		TextSpan previous = null;
		for (TextSpan span: textSpans) {
			if (previous == null) {
				sb.append(span.text);
			} else if ((span.start - previous.end) <= 1) {
				sb.append(" ");
				sb.append(span.text);
			} else {
				sb.append(" ... ");
				sb.append(span.text);
			}
			previous = span;
		}
		if (textSpans.size() == 0) {
			return new TextSpan(0, 0, "");
		} else {
			return new TextSpan(textSpans.get(0).start, textSpans.get(textSpans.size()-1).end, sb.toString());
		}
	}

	/**
	 * Sort a new text span into a list of text spans.
	 * 
	 * @param spans The sorted list of spans
	 * @param span The new span to add
	 * @return The sorted list of spans
	 */
	private static ArrayList<TextSpan> sortIn(ArrayList<TextSpan> spans, TextSpan span) {
		if (spans.isEmpty()) {
			spans.add(span);
		} else {
			boolean found = false;
			for (int i=0; i<spans.size(); i++) {
				if (span.end <= spans.get(i).start) {
					spans.add(i, span);
					found = true;
					break;
				}
			}
			if (!found) spans.add(span);
		}
		return spans;
	}

}
/**
 * A local class to hold a span of text along with its document start and end position.
 * 
 * @author Jim Firby
 */
class TextSpan {
    	
    	int start = 0;
    	int end = 0;
    	String text = null;
    	//String source = null;
    	
    	TextSpan(int start, int end, String text) { //String source) {
    		this.start = start;
    		this.end = end;
    		this.text = text;
    		//this.source = source;
    	}
    	
    	//TextSpan(int start, int end, String text) {
    	//	this(start, end, text, null);
    	//}

}

