/*
 * ReferenceProcessor.java
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
import java.util.Collection;
import java.util.List;

import edu.uchsc.ccp.opendmap.Parser;
import edu.uchsc.ccp.opendmap.Reference;

/**
 * Get the the desired references that DMAP has recognized.  Order
 * them according to a simple score that prefers references that
 * include every word over the span that was used to recognize
 * them.
 * 
 * @author R. James Firby
 */
public class ReferenceProcessorNoTemplateMerging {
public static final boolean DEBUG = false;
	/**
	 * Get the desired references ordered by score.
	 * 
	 * @param parser The parser that has processed an utterance and is holding the references.
	 * @param interests The references of interest.  A collection of reference types.
	 * @param debug True if detailed debugging information should be printed.
	 * @return The ordered list of references of interest.
	 */
	public static Collection<Reference> getRecognizedReferences(Parser parser, Collection<String> interests, boolean debug) {
		// Grab up any generated output
		if (debug) {
			// Print out everything we saw
			Collection<Reference> all = parser.getReferences();
			System.out.println("\nAll references:");
			for (Reference r: all) {
				System.out.println(r.getStart() + ".." + r.getEnd() + " " + r.getReferenceString());
			}
		}
	
		// Find all of the unsubsumed references that were generated
		Collection<Reference> references = parser.getUnsubsumedReferences(debug);
		if (debug) {
			System.out.println("\nUnsubsumed references");
			for (Reference r: references) {
				System.out.println(r.getStart() + ".." + r.getEnd() + " " + r.getReferenceString());
			}
		}
		
		// Extract and score any interesting references found
		ArrayList<Reference> seen = new ArrayList<Reference>();
		for (Reference r: references) {
			if (isConceptOfInterest(r, interests)) {
				// Sort into list
				boolean done = false;
				for (int i=0; i<seen.size(); i++) {
					Reference ref = seen.get(i);
					if (calculateScore(r) > calculateScore(ref)) {
						seen.add(i, r);
						done = true;
						break;
					}
				}
				if (!done) seen.add(r);
			}
		}
		
		// Done
		return seen;
	}

	
	
	
	/**
	 * This looks at all references, not just the unsubsumed references
	 * Get the desired references ordered by score.
	 * 
	 * @param parser The parser that has processed an utterance and is holding the references.
	 * @param interests The references of interest.  A collection of reference types.
	 * @param debug True if detailed debugging information should be printed.
	 * @return The ordered list of references of interest.
	 */
	public static Collection<Reference> getRecognizedAllReferences(Parser parser, Collection<String> interests, boolean debug) {
		// Grab up any generated output
		if (debug) {
			// Print out everything we saw
			Collection<Reference> all = parser.getReferences();
			System.out.println("\nAll references:");
			for (Reference r: all) {
				System.out.println(r.getStart() + ".." + r.getEnd() + " " + r.getReferenceString());
			}
		}
	
//		// Find all of the unsubsumed references that were generated
//		Collection<Reference> references = parser.getUnsubsumedReferences(debug);
//		if (debug) {
//			System.out.println("\nUnsubsumed references");
//			for (Reference r: references) {
//				System.out.println(r.getStart() + ".." + r.getEnd() + " " + r.getReferenceString());
//			}
//		}
		
		// Extract and score any interesting references found
		ArrayList<Reference> seen = new ArrayList<Reference>();
		for (Reference r: parser.getReferences()) {
			if (isConceptOfInterest(r, interests)) {
				debug("CONCEPT OF INTEREST: " + r.getReferenceString() + " -- SCORE: " + calculateScore(r));
				// Sort into list
				boolean done = false;
				for (int i=0; i<seen.size(); i++) {
					Reference ref = seen.get(i);
					if (calculateScore(r) > calculateScore(ref)) {
						seen.add(i, r);
						done = true;
						break;
					}
				}
				if (!done) seen.add(r);
			}
		}
		
		debug("Sorted (by score) list of CONCEPTS OF INTEREST");
			for (Reference seenRef : seen) {
				debug("SORTED CONCEPT OF INTEREST: " + seenRef.getReferenceString());
		}
		
		debug("Keeping references that are not subsumed...");
		/* Now check for subsumption */
		List<Reference> keep = new ArrayList<Reference>();
		for (Reference ref : seen) {
			boolean isSubsumed = false;
//        	System.out.println("CHECKING: "+ ref.getReferenceString());
        	for (Reference sref : seen) {
        		if (sref.subsumes(ref, true)) {
        			debug(ref.getReferenceString() + " IS SUBSUMED BY " + sref.getReferenceString());
        			isSubsumed = true;
        			break;
        		}
        	}
        	if (!isSubsumed) {
        		debug("REFERENCE IS NOT SUBSUMED, KEEPING: " + ref.getReferenceString());
        		keep.add(ref);
        	}
        }
		
		// Done
		return keep;
	}
	
	
	/**
	 * Check whether a reference is a concept of interest.
	 * 
	 * @param r The reference to check.
	 * @param interests The collection of concepts of interest
	 * @return True if the reference is a concept of interest
	 */
	private static boolean isConceptOfInterest(Reference r, Collection<String> interests) {
		// If no interests specified, return everything
		if ((interests == null) || (interests.size() <= 0)) return true;
		// Check whether the reference is an interesting concept
		boolean found = false;
		for (String interest: interests) {
			if (r.isa(interest)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Score the reference.
	 * 
	 * @param r The reference
	 * @return The score
	 */
	public static double calculateScore(Reference r) {
		return ((double)(calculateSpan(r) - r.getMissing())) / ((double)(calculateSpan(r)));
	}
	
	/**
	 * Calculate the span of a reference.
	 * 
	 * @param r The reference
	 * @return The span
	 */
	private static int calculateSpan(Reference r) {
		return r.getEnd() - r.getStart() + 1;
	}
	
	
	private static void debug(String message) {
		if (DEBUG) {
			System.err.println("DEBUG -- ReferenceProcessor: " + message);
		}
	}
}
