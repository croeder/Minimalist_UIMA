/*
 * DMAPStanfordPhraseGraph.java
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

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;

import edu.uchsc.ccp.opendmap.dependency.DMAPDependencyGraphFunctionHandler;
import edu.uchsc.ccp.opendmap.dependency.DependencyRelation;


/**
 * This class packages up a Stanford Phrase Tree as a DMAP Property Function Handler.
 * <p>
 * The goal with this class is to make syntactic constraints that the Stanford Parser identifies available to DMAP to
 * use as constraints between the fillers of separate slots. For example:
 * 
 * <pre>
 *    {c-statement} := [subject dep:x] [action head:x] [object dep:x];
 * </pre>
 * 
 * This pattern is intended to mean that a c-statement can be recognized when DMAP sees a [subject] an [action] and an
 * [object]. However, in addition, the property function constraints on [subject] and [object] says that they must be
 * syntactically "dependent" parts of the phrase encompassing the [action] (the head of the phrase).
 * <p>
 * The syntactic property functions "dep" and "head" only make sense in the context of some sort of syntactic
 * information. This StanfordPhraseGraph class defines these two property functions and uses the information from the
 * Stanford Parser to evaluate them.
 * <p>
 * In future, it is possible to add additional syntactic functions like "subj:x" and "obj:x" to use more Stanford Parser
 * information.
 * <p>
 * <i>Note</i> The main problem with this at the moment is that the Stanford Parser is often wrong about these syntactic
 * assignments in GeneRIFs.
 * 
 * @author R. James Firby
 */
public class DMAPJcasDependencyConstraintHandler extends DMAPDependencyGraphFunctionHandler {

	/**
	 * Create a new DMAPStanfordPhraseGraph property function handler from the annotations the Stanford Parser has left
	 * in the UIMA CAS.
	 * 
	 * @param jcas
	 *            The UIMA JCas holding the Stanford Parser annotations.
	 * @param debug
	 *            True if debugging information should be printed.
	 */
	public DMAPJcasDependencyConstraintHandler(JCas jcas, boolean debug) {
		super();
		this.debug = debug;
		Collection<DependencyRelation> dependencyRelations = new ArrayList<DependencyRelation>();
		// Grab an iterator over the set of TokenAnnotation structures in the JCas
		//FSIterator tokenIter = jcas.getJFSIndexRepository().getAnnotationIndex(TokenAnnotation.type).iterator();
		//while (tokenIter.hasNext()) {
			//Object thing = tokenIter.next();
			//if (thing instanceof TokenAnnotation) {
			//	TokenAnnotation ccpToken = (TokenAnnotation) thing;
			//	Collection<TypedDependency> typeDependenciesForToken = getTypedDependenciesForToken(ccpToken, jcas);
			//	dependencyRelations.addAll(getDependencyRelationsForTypedDependencies(typeDependenciesForToken));
			//}
		//}

		//initializeDependencyGraph(dependencyRelations);

	}

	/*
	 * Retrieves a collection of TypedDependency objects from a TokenAnnotation
	 * 
	 * @param ccpToken
	 * @param jcas
	 * @return
	 */

	//private Collection<TypedDependency> getTypedDependenciesForToken(TokenAnnotation ccpToken, JCas jcas) {
	//	Collection<TypedDependency> typedDependenciesToReturn = new ArrayList<TypedDependency>();

		/* NOTE: The following line is correct, but currently breaks the DMAP dependency constraint functionality
			because the stanford parser is placing the typed dependency info in the annotation metadata properities
		 	instead of the token properties. This is correct because the token properties are going to go away, but for
		 	now we will grab the typed dependency info from the token properties.
		*/
	//	 Collection<TypedDependencyProperty> typedDependencyProperties 
	//	 	= UIMA_Annotation_Util.getAnnotationProperties(
	//			ccpToken, TypedDependencyProperty.class, jcas);


		//Collection<TypedDependencyProperty> typedDependencyProperties 
		//		= UIMASyntacticAnnotation_Util.getTokenTypedDependencyProperties(ccpToken);

	//	for (TypedDependencyProperty tdp : typedDependencyProperties) {
	//		typedDependenciesToReturn.add(tdp.getTypedDependency());
	//	}
	//	return typedDependenciesToReturn;
	//}

//	private Collection<DependencyRelation> getDependencyRelationsForTypedDependencies(Collection<TypedDependency> typedDependencies) {
//		Collection<DependencyRelation> dependencyRelations = new ArrayList<DependencyRelation>();
//		for (TypedDependency td : typedDependencies) {

			/*
			 * The Strings can be blank here as they are only used for printing debug output.. the token numbers are
			 * what are most important.
			 */

//			dependencyRelations.add(new DependencyRelation(td.getRelation(), td.getGovernorTokenNum(), "", td.getDependentTokenNum(), ""));
//		}
//		return dependencyRelations;
//
//	}

}
