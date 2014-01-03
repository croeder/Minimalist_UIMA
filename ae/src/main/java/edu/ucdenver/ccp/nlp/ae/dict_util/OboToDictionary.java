
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
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;;

import org.apache.commons.lang3.StringEscapeUtils;

import org.obo.dataadapter.DefaultOBOParser;
import org.obo.dataadapter.OBOParseEngine;
import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.Namespace;
import org.obo.datamodel.IdentifiedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.impl.OBOClassImpl;
import org.obo.datamodel.Synonym;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.PropertyValue;
import org.obo.datamodel.UnknownStanza;



/**
 * A utility for building an XML-formatted dictionary of terms in an OBO ontology.
 * 
 * @author Karin Verspoor
 * @author Chris Roeder
 * 
 */
public class OboToDictionary {

	// don't confuse with org.geneontology.oboedit.datamodel.Synonym.getScope()
	public enum SynonymType {
		EXACT_ONLY,
		ALL
	}

	private static final Logger logger = Logger.getLogger(OboToDictionary.class);

	private boolean filterSingleLetterTerms = true;
	private SynonymType synonymType;
	private Set<String> namespacesToInclude;

	public OboToDictionary() {
		filterSingleLetterTerms = true;
		synonymType = SynonymType.EXACT_ONLY; 
		namespacesToInclude = null;
	}

	public OboToDictionary(
						   boolean filterSingleLetterTerms,
						   SynonymType synonymType,
						   Set<String> namespacesToInclude) {
		this.filterSingleLetterTerms = filterSingleLetterTerms;
		this.synonymType = synonymType;
		this.namespacesToInclude = namespacesToInclude;
	}


	public void convert(File oboFile, File outputFile)
		throws IOException, FileNotFoundException, OBOParseException {

		DefaultOBOParser parser = new DefaultOBOParser();
		OBOParseEngine engine = new OBOParseEngine(parser);
		List<String> paths = new ArrayList<String>();
		paths.add(oboFile.getAbsolutePath());
		engine.setPaths(paths);
		engine.parse();
		OBOSession session = parser.getSession();
		BufferedWriter writer = 
			new BufferedWriter(
				new OutputStreamWriter(
					new FileOutputStream(
						outputFile, true),
					Charset.forName("UTF-8").newEncoder()
						.onMalformedInput(CodingErrorAction.REPORT)
                		.onUnmappableCharacter(CodingErrorAction.REPORT)));

		
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
		writer.newLine();

		buildEntries( session.getObjects(), writer);

		writer.write("</synonym>\n");
		writer.close();
	}

	//http://oboedit.org/api/obo/
	/**
     * appends the writer with entries in the iterator
     *
	 * @param oboClsIter
	 * @param writer
	 * @throws OBOParseException
	 */
	public void buildEntries(Collection<IdentifiedObject> objects , BufferedWriter writer) 
		throws OBOParseException, IOException  {

		for (IdentifiedObject object : objects) {
			if (object != null 
				&& object.getType().getName().startsWith("obo:TERM") 
				&& !object.getName().startsWith("obo:")) {

				OBOClassImpl oboClass = (OBOClassImpl) object;
				if (!oboClass.isObsolete()) {
					if (namespacesToInclude == null || namespacesToInclude.isEmpty()) {
						writer.write(oboToXml(oboClass.getID(), oboClass));
					}
					else {
						Namespace objNS = oboClass.getNamespace();
						if (objNS != null 
							&& namespacesToInclude.contains(objNS.toString())) {
							writer.write(oboToXml(oboClass.getID(), oboClass));
						}
					}
				}
			}
		}
	}

	/**
	 * Represent the OBO object as a XML dictionary string.
	 * 
	 * @param id
	 *            the ID of the OBO object
	 * @param oboObj
	 *            the OBO object itself
	 * @param synonymType
	 * @return an XML-formatted string in the ConceptMapper Dictionary format.
	 */
	private String  oboToXml(String id, OBOClass oboObj) {
		
		StringBuffer buf = new StringBuffer();
		String name = oboObj.getName();

		// id without a name. Don't add to dictionary.
		if (name == null || name == "" || name == "<new term>") {
			logger.warn("oboToXML() null name: " + name);
			return "";
		}

		// single letter name? Don't add to dictionary
		if (filterSingleLetterTerms && name.length() <= 1) {
			logger.warn("oboToXML() short name: " + name);
			return "";
		}

		name = StringEscapeUtils.escapeXml(name);
		buf.append("<token id=\"" + id + "\"");
		buf.append(" canonical=\"" + name + "\"" + ">\n");

		Pattern endsWithActivityPattern = Pattern.compile("(.*)\\sactivity");	
		{
			Matcher m = endsWithActivityPattern.matcher(name);
			if (m.matches()) {
				String enzyme = m.group(1);
				buildSynonymLine(enzyme, buf);
			}
		}

		buildSynonymLine(name, buf); // needed?
		if (name.contains("_")) {
			buildSynonymLine(name.replace('_', ' '), buf);
		}
		for (Object synObj : oboObj.getSynonyms()) {
			Synonym syn = (Synonym) synObj;
			if (synonymType.equals(SynonymType.ALL) 
				|| (synonymType.equals(SynonymType.EXACT_ONLY) 
					&& syn.getScope() == syn.EXACT_SYNONYM)) {
				String synonymStr = StringEscapeUtils.escapeXml(syn.getText());
				buildSynonymLine(synonymStr, buf);
				if (name.contains("_")) {
					buildSynonymLine(synonymStr.replace('_', ' '), buf);
				}
				Matcher m = endsWithActivityPattern.matcher(synonymStr);
				if (m.matches()) {
					String enzyme = m.group(1);
					buildSynonymLine(enzyme, buf);
				}
			}
		}

		buf.append("</token>\n");

        return buf.toString();
	}


	// <variant base="foo" />
	private String buildSynonymLine(String name, StringBuffer buf) {
		if (filterSingleLetterTerms && name.length() <= 1)
			return "";

		buf.append("\t<variant base=\"");
		buf.append(name);
		buf.append("\"/>\n");

		return buf.toString();
	}

	public static void usage() {
		System.out.println("mvn -f pom-obo.xml  -e exec:java -Dinput=<file> -Doutput=<file> [<namespace name>]");
		System.out.println("   namespace names for GO are: bi logical_process, cellular_component, and molecular_function");
	}

	public static void main(String args[]) {

		BasicConfigurator.configure();

		if (args.length < 2) {
			usage();
		}
		else {
			try {
				File oboFile = new File(args[0]);
				File outputFile = new File(args[1]);
				String namespaceName = "";
				if (args.length > 2) {
					namespaceName = args[2];
				}
				if (!oboFile.canRead()) {
					System.out.println("can't read input file;" + oboFile.getAbsolutePath());
					usage();
					System.exit(-2);
				}
				if (outputFile.exists() &&  !outputFile.canWrite()) {
					System.out.println("can't write output file;" + outputFile.getAbsolutePath());
					usage();
					System.exit(-3);
				}

				logger.warn("running with: " + oboFile.getAbsolutePath());
				OboToDictionary converter = null;	
				if (namespaceName != null && namespaceName.length() > 0 ) {
					Set<String> namespaceSet = new TreeSet<String>();
					namespaceSet.add(namespaceName);
					converter = new OboToDictionary(true,
						SynonymType.EXACT_ONLY,
						namespaceSet);
				}
				else {
					converter = new OboToDictionary();
				}
				converter.convert(oboFile, outputFile);
			}
			catch (Exception e) {
				System.out.println("error:" + e);
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

}

