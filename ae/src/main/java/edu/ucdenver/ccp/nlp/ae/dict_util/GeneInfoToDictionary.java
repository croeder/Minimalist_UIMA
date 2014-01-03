
/* Copyright (C) 2007-2010, 2012 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
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
 */

package edu.ucdenver.ccp.nlp.ae.dict_util;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 * Create a ConceptMapper dictionary from EntrezGene's gene_info file.
 *   http://www.ncbi.nlm.nih.gov/books/NBK3841/#EntrezGene.Tips_for_Programmers
 *   http://uima.apache.org/downloads/sandbox/ConceptMapperAnnotatorUserGuide/ConceptMapperAnnotatorUserGuide.html
 * 
 * @author Chris Roeder
 * 
 */
public class GeneInfoToDictionary {

	private static final Logger logger = Logger.getLogger(GeneInfoToDictionary.class);

	private boolean filterSingleLetterTerms = true;
	private Set<String> namespacesToInclude;
	private HashMap<String, List<String>> idToSynonymMap = new HashMap<String, List<String>>();
	private HashMap<String, List<String>> synonymToIdMap = new HashMap<String, List<String>>();

	public GeneInfoToDictionary(File geneFile)  
	throws IOException { 
		filterSingleLetterTerms = true;
		namespacesToInclude = null;

		BufferedReader reader = null;
		try {

			// Create synonyms hash from 5th and 14th columns. They have duplicates, so create
			// a back hash so you can see how many id's share a particular other designation.
			// 5th col. is synonyms. 14th is Other Designations
			//reader = FileReaderUtil.initBufferedReader(geneFile, CharacterEncoding.UTF_8);
			CharsetDecoder csd = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
			InputStream ins = new FileInputStream(geneFile);
       		reader = new BufferedReader(new InputStreamReader(ins,csd));
			reader.readLine();
			while (reader.ready()) {	
				String line = reader.readLine();
				String[] parts = line.split("\t");
				String id = parts[1];
				String[] synonyms = parts[4].split("\\|");
				String[] other = parts[13].split("\\|");
				ArrayList<String> allSynonyms = new ArrayList<String>();
	
				if (!parts[4].equals("-")) {
					allSynonyms.addAll(Arrays.asList(synonyms));
				}
				if (!parts[13].equals("-")) {
					allSynonyms.addAll(Arrays.asList(other));
				}
	
	
				for (String syn : allSynonyms) {
					if (idToSynonymMap.get(id) == null) {
						idToSynonymMap.put(id, new ArrayList<String>());
					}
					idToSynonymMap.get(id).add(syn);
	
					if (synonymToIdMap.get(syn) == null) {
						synonymToIdMap.put(syn, new ArrayList<String>());
					}
					synonymToIdMap.get(syn).add(id);
				}
			}
		}
		finally {
			try {
				reader.close();
			}
			catch (Exception x) {
			}
		}
		
	}


	public void convert(File geneFile, File outputFile)
	throws IOException, FileNotFoundException {

		BufferedReader reader = null;
		try {
			CharsetDecoder csd = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
			InputStream ins = new FileInputStream(geneFile);
       		reader = new BufferedReader(new InputStreamReader(ins,csd));
	
			// Create Dictionary
			BufferedWriter writer = 
				new BufferedWriter(
					new OutputStreamWriter(
						new FileOutputStream(
							outputFile, true),
						Charset.forName("UTF-8").newEncoder()
							.onMalformedInput(CodingErrorAction.REPORT)
                			.onUnmappableCharacter(CodingErrorAction.REPORT)));
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			writer.write("<synonym>\n");

			reader.readLine();
			while (reader.ready()) {	
				String line = reader.readLine();
				//Pair<id,name>
				ImmutablePair<String, String> data = parseLine(line);
				String entry = createEntry(data.left, data.right);	
				writer.write(entry);
			}

			writer.write("</synonym>\n");
			writer.close();
		}
		finally {
			try {
				reader.close();
			}
			catch (Exception x) {
			}
		}
	}


	//Pair<id, name>
	private ImmutablePair<String, String> parseLine(String line) {
		try {
			String[] parts = line.split("\t");

			String id = parts[1];
			String name = parts[2];
			return ImmutablePair.of(id, name);	
		}
		catch (Exception x) {
			System.out.println("error: " + x);
			System.out.println("LINE:" + line);
			x.printStackTrace();
			throw new RuntimeException(x);	
		}
	}


	/**
	 * Represent the concept as a XML dictionary string.
	 * 
	 * @param  the ID of the object
	 * @param synonyms
	 * @return an XML-formatted string in the ConceptMapper Dictionary format.
	 */
	private String createEntry(String id, String name) {
		
		StringBuilder buf = new StringBuilder();

		id = StringEscapeUtils.escapeXml(id);
		name = StringEscapeUtils.escapeXml(name);
		buf.append("<token id=\"" + id + "\"");
		buf.append(" canonical=\"" + name + "\"" + ">\n");

		if (idToSynonymMap.get(id) != null) {
			for (String s : idToSynonymMap.get(id)) {

				if (synonymToIdMap.get(s).size() < 3) {
					buildSynonymLine(s, buf);
					if (s.contains("_")) {
						buildSynonymLine(s.replace('_', ' '), buf);
					}
				}
			}
		}

		buf.append("</token>\n");

        return buf.toString();
	}


	// <variant base="foo" />
	private String buildSynonymLine(String name, StringBuilder buf) {
		if (filterSingleLetterTerms && name.length() <= 1) {
			return "";
		}
		name = StringEscapeUtils.escapeXml(name);

		buf.append("\t<variant base=\"");
		buf.append(name);
		buf.append("\"/>\n");

		return buf.toString();
	}




	public static void usage() {
		System.out.println("mvn -f pom-gene_info.xml  -e exec:java -Dinput=<file> -Doutput=<file> [<namespace name>]");
		System.out.println("   namespace names for GO are: bi logical_process, cellular_component, and molecular_function");
	}

	public static void main(String args[]) {

		BasicConfigurator.configure();

		if (args.length < 2) {
			usage();
		}
		else {
			try {
				File geneFile = new File(args[0]);
				File outputFile = new File(args[1]);
				if (!geneFile.canRead()) {
					System.out.println("can't read input file;" + geneFile.getAbsolutePath());
					usage();
					System.exit(-2);
				}
				if (outputFile.exists() &&  !outputFile.canWrite()) {
					System.out.println("can't write output file;" + outputFile.getAbsolutePath());
					usage();
					System.exit(-3);
				}

				logger.warn("running with: " + geneFile.getAbsolutePath());
				GeneInfoToDictionary converter = new GeneInfoToDictionary(geneFile);
				converter.convert(geneFile, outputFile);
			}
			catch (Exception e) {
				System.out.println("error:" + e);
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

}

