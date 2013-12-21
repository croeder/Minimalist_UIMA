/*
 * TestSummarizer.java
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
import java.util.HashMap;
import java.util.Iterator;

import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.ClassMention;

/**
 * This class prints out selected contents of the CAS in the standard
 * annotation summery format.
 * 
 * @author R. James Firby
 */
public class TestSummarizer {
	
	private static final String MENTION_SEPARATOR = ";";
	private static final String SLOT_SEPARATOR = ",";
	
	/* The mentions in the CAS to print */
	private HashMap<String, String[]> mentionsOfInterest = null;
	
	/**
	 * Create a new TestSummarizer that will generate summary strings for the mentions and slots
	 * specified in the pattern.
	 * <p>
	 * A mention pattern has the form:
	 * <pre>
	 *   type,slot,slot;type,slot,slot;...
	 * </pre>
	 * Where 'type' is the name of a mention type to print, and the 'slot's
	 * following the 'type' are thje slots for that type to include in the
	 * output.
	 * 
	 * @param mentionPattern The pattern defining the mention types and slots to print.
	 */
	public TestSummarizer(String mentionPattern) {
		if (mentionPattern != null) {
    	String mentionTypes[] = mentionPattern.split(MENTION_SEPARATOR);
    	if ((mentionTypes != null) && (mentionTypes.length > 0)) {
    		mentionsOfInterest = new HashMap<String, String[]>();
    		for (int i=0; i<mentionTypes.length; i++) {
    			String typeParts[] = mentionTypes[i].split(SLOT_SEPARATOR);
    			if ((typeParts != null) && (typeParts.length > 0)) {
    				String typeName = typeParts[0].trim().toLowerCase();
    				if (typeParts.length > 1) {
    					String slotNames[] = new String[typeParts.length-1];
    					for (int j=1; j<typeParts.length; j++)
    						slotNames[j-1] = typeParts[j].trim();
    					mentionsOfInterest.put(typeName, slotNames);
    				} else {
    					mentionsOfInterest.put(typeName, null);
    				}
    			}
    		}
    	}
    }
	}
	
	/**
	 * Generate summary strings for all annotations in the JCAS that are described in the
	 * pattern string for this summarizer.
	 * 
	 * @param docId The document ID that has been analyzed to fill the JCas
	 * @param jcas The JCas holding the annotations to be summarized
	 * @return A set of summary strings, one for each annotation of interest in the JCas
	 */
	public ArrayList<String> summarizeJCas(String docId, JCas jcas) {
		ArrayList<String> summary = new ArrayList<String>();
		// Get the GeneRIF text
		String docText = jcas.getDocumentText();
	  // Print out all output annotations for this generif
	  Iterator annotationIter = jcas.getJFSIndexRepository().getAnnotationIndex(TextAnnotation.type).iterator();
	  while ((annotationIter != null) && annotationIter.hasNext()) {
	  	Object thing = annotationIter.next();
	  	if (thing instanceof TextAnnotation) {
	  		TextAnnotation annotation = (TextAnnotation) thing;
	  		if (isAnnotationOfInterest(annotation)) {
	  			// Get the annotation string and print it
	  			AnnotationString annotationString = new AnnotationString(annotation, slotsOfInterest(annotation), docId, docText);
	  			summary.add(annotationString.toString());
	  		}
	  	}
	  }
	  // Done
	  return summary;
	}
	
	/**
	 * Checks whether and annotation is specified in the pattern string for
	 * this summarizer.
	 * 
	 * @param annotation The annotation to check.
	 * @return 'True' if this annotation should be summarized in the output.
	 */
  private boolean isAnnotationOfInterest(TextAnnotation annotation) {
  	// If nothing was specified, default to everything
  	if (mentionsOfInterest == null) return true;
  	// See if this annotations mention is of interest
  	ClassMention mention = annotation.getClassMention();
		String className = mention.getMentionName();
		return ((className != null) && mentionsOfInterest.containsKey(className.trim().toLowerCase()));
  }
  
  /**
   * Get all the slots that should be included in the summary for this annotation.
   * 
   * @param annotation The annotation to summarize.
   * @return The set of slots to include in the summary.
   */
  private String[] slotsOfInterest(TextAnnotation annotation) {
  	// If nothing was specified, default to everything
  	if (mentionsOfInterest == null) return null;
  	// See if this annotations mention is of interest
  	ClassMention mention = annotation.getClassMention();
		String className = mention.getMentionName();
		if (className != null) {
			return mentionsOfInterest.get(className.trim().toLowerCase());
		} else {
			return null;
		}
	}

}
