/*
 * OpenDmapAnnotator.java
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
import java.util.Vector;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

import edu.uchsc.ccp.opendmap.Parser;
import edu.uchsc.ccp.opendmap.configuration.Configuration;
import edu.uchsc.ccp.opendmap.configuration.ConfigurationException;
import edu.uchsc.ccp.opendmap.pattern.ParseException;

/**
 * This class is a wrapper for OpenDMAP to help fit it into the UIMA framework.
 * <p>
 * Right now this class hides many of the details involved in setting up the parser, connecting it to Protege, and
 * loading pattern definitions.
 * <p>
 * To build a UIMA annotator with OpenDMAP, extend this class.
 * 
 * @author R. James Firby
 */
public abstract class OpenDmapAnnotator extends JCasAnnotator_ImplBase {
	private static Logger logger = Logger.getLogger(OpenDmapAnnotator.class);

	public static final String PARAM_CONFIGURATION_FILE = "configurationFile";
	
	/* The parser instance to use in the UIMA pipeline. */
	private Parser parser = null;

	/* The Protege project and pattern files to load into the parser */
	private String protegeProjectFile = null;
	private Vector<String> patternFiles = null;

	/**
	 * Create a new, empty OpenDMAP Annotator.
	 */
	public OpenDmapAnnotator() {
		super();
		protegeProjectFile = null;
		patternFiles = new Vector<String>();
	}

	/**
	 * Set the Protege Project file to be loaded into the OpenDMAP parser when this annotator is initialized. Only one
	 * project file may be used for each annotator.
	 * 
	 * @param filename
	 *            The name of the Protege project file
	 */
	public void setProtegeProjectFilename(String filename) {
		protegeProjectFile = filename;
	}

	/**
	 * Add the name of an OpenDMAP pattern file to be loaded into the parser when this annotator is initialized. Any
	 * number of pattern files may be added.
	 * 
	 * @param filename
	 *            The name of one pattern file
	 */
	public void addPatternFilename(String filename) {
		patternFiles.add(filename);
	}

	/**
	 * Initialize the OpenDMAP parser and load the Protege project and the pattern files.
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		// Create a parser that ignores case (for ease of typing)
		// and generates tracing output (if desired).
		// parser = new Parser(false, Level.FINEST);
		parser = new Parser(false, Level.OFF);

		// If a configuration file is specified, load it
		String configurationFile = null;
		// Get the configuration filename from the UIMA context
		Object configFile = context.getConfigParameterValue(PARAM_CONFIGURATION_FILE);
		if (configFile == null) {
			logger.error("Cannot get OpenDMAP configuration file name from UIMA context");
		} else {
			if (configFile instanceof String) {
				configurationFile = (String) configFile;
			}
		}
		if (configurationFile != null) {
			// Configure the parser using the configuration file
			try {
				Configuration configurator = new Configuration(configurationFile);
				configurator.configure(parser);
			} catch (ConfigurationException e) {
				// Can't process configuration

				throw new ResourceInitializationException("Cannot initialize OpenDMAP from configuration file '" + configurationFile
						+ "'. Error message: " + e.getMessage(), new Object[] { this });
			}
		}

		// Add recognition patterns to the parser
		if (!patternFiles.isEmpty()) {
			Collection errors = new ArrayList();
			Project project = new Project(protegeProjectFile, errors);
			if (errors.size() != 0) {
				// Throw the first error if one encountered
				Object e = errors.iterator().next();
				if (e instanceof Throwable) {
					throw new ResourceInitializationException("Creating Protege project '" + protegeProjectFile + "'",
							new Object[] { this }, (Throwable) e);
				} else {
					throw new ResourceInitializationException("Creating Protege project '" + protegeProjectFile + "'", e.toString(),
							new Object[] { this });
				}
			} else {
				// Load the pattern files
				KnowledgeBase kb = project.getKnowledgeBase();
				for (String patternFile : patternFiles) {
					try {
						parser.addPatternsFromFile(patternFile, kb);
					} catch (ParseException e) {
						// Throw an error if one occurs
						throw new ResourceInitializationException("Loading pattern file '" + patternFile + "'. Error Message: "
								+ e.getMessage(), new Object[] { this }, e);
					}
				}
			}
		}

		// At this point, the parser is ready to go
		super.initialize(context);
	}

	/**
	 * When the UIMA pipeline signals it is time to run this annotator, call user code to get tokens from the jcas, call
	 * the parser, and then write annotations back to the jcas.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// public void process(JCas jcas, ResultSpecification resultSpec) throws AnnotatorProcessException {
		// Reset the parser
		parser.reset();
		// Do the real work
		process(parser, jcas);
	}

	/**
	 * Process the JCas as part of a UIMA pipeline. Ie. get input tokens for OpenDMAP from the JCas, run the OpenDMAP
	 * parser, process the results and write appropriate annotations back to the JCas.
	 * 
	 * @param parser
	 *            The OpenDMAP parser to use for parsing.
	 * @param jcas
	 *            The JCas holding input tokens and output annotations.
	 * @param resultSpec
	 *            The type of result expected to be put into the JCas.
	 */
	public abstract void process(Parser parser, JCas jcas);

}
