/*
 * AnnotationMap.java
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
import java.io.IOException;
import java.io.PrintStream;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import edu.uchsc.ccp.opendmap.DMAPItem;
import edu.uchsc.ccp.opendmap.Reference;

/**
 * This class loads a translation map for use in moving from
 * CAS annotations to OpenDMAP input tokens and from OpenDMAP
 * output references to CAS annotations.
 * 
 * @author R. James Firby
 */
public class AnnotationMap {
	
	/* The set of CAS annotations to use as input tokens and the slots to include */
	private HashMap<String, String> inputMap = null;
	private HashMap<String, HashMap<String, String>> inputSlotMap = null;
	
	/* A set of regular expressions for CAS annotations to be included */
	private Vector<Pattern> inputRegEx = null;
	
	/* The set of output references to turn into CAS annotations and the slots to include */
	private HashMap<String, String> outputMap = null;
	private HashMap<String, HashMap<String, String>> outputSlotMap = null;

	/* */
	private HashMap<String, String> referenceRoots = null;
	/* Cached reference types of interest from the outputMap */
	private Vector<String> referencesOfInterest = null;
	
	/**
	 * Create an empty annotation map.  Mappings will need to be added explicitly.
	 */
	public AnnotationMap() {}

	/**
	 * Create an annotation map from an XML file.
	 * 
	 * @param filename The name of the file holding the configuration.
	 * @throws ParserConfigurationException If there is no good XML parser available
	 * @throws SAXException If there is an XML syntax problem in the configuration file
	 * @throws IOException If there is a problem reading the file
	 */
	public AnnotationMap(String filename) throws ParserConfigurationException, SAXException, IOException {
		// Turn the filename into a File
		File file = new File(filename);
		Document document = readConfigurationFile(file);
		// Process the input annotation map
		NodeList nodes = document.getElementsByTagName("input-annotation");
		buildInputAnnotationMap(nodes, file);
		// Process the output annotation map
		nodes = document.getElementsByTagName("output-annotation");
		buildOutputAnnotationMap(nodes, file);
	}
	
	/**
	 * Check whether any CAS annotations other than TokenAnnotations are to be 
	 * used as input tokens (typically named entities).
	 * 
	 * @return 'True' if there is at least one CAS annotation type to use
	 */
	public boolean hasInputMentionsOfInterest() {
		return ( ((inputMap != null) && (inputMap.size() > 0)) ||
				 ((inputRegEx != null) && (inputRegEx.size() > 0)) );
	}
	
	/**
	 * Check whether a specific CAS annotation mention is to become an input
	 * token.
	 * 
	 * @param name The name of the CAS annotation mention
	 * @return 'True' if this type of annotation mention should become a token
	 */
	public boolean isInputMentionOfInterest(String name) {
		boolean ofInterest = false;
		if ((name == null) || ( (inputMap == null) && ( inputRegEx == null ) ) ) {
			return false;
		} else {
			if ( inputMap != null ){
				ofInterest = inputMap.containsKey(canonicalize(name));
			}
			
			if ( !ofInterest && ( inputRegEx != null) ) {
				for (Pattern mentionPattern : inputRegEx) {
					if ( mentionPattern.matcher(name).matches() ) {
						ofInterest = true;
						break;
					}
				}
			}
		}
		return ofInterest;
	}
	
	/**
	 * Get the type of DMAP Protege Frame to use for the input token corresponding
	 * to this type of CAS annotation mention.
	 * 
	 * @param name The name of the CAS annotation mention
	 * @return The DMAP Protege Frame type to represent this annotation
	 */
	public String getInputMentionReferenceType(String name) {
		if ((name == null) || ( (inputMap == null) && ( inputRegEx == null ) ) ) {
			return null;
		} else {
			String canName = canonicalize(name);
			if ( inputMap != null && inputMap.containsKey(canName)){
				return inputMap.get(canonicalize(name));
			}
			
			// Check to see whether a Regular Expression matches
			// If so, just return the canonical name: map to same frame by default
			if ( inputRegEx != null ) {
				for (Pattern mentionPattern : inputRegEx) {
					if ( mentionPattern.matcher(name).matches() ) {
						return name;
					}
				}
			}
		}
		return null;	
	}
	
	/**
	 * Set the type of DMAP Protege Frame to use for the input token corresponding
	 * to this type of CAS annotation mention.
	 * 
	 * @param name The name of the CAS annotation mention
	 * @param dmapType The DMAP Protege Frame type to represent this annotation
	 */
	public void setInputMentionReferenceType(String name, String dmapType) {
		if ((name != null) && (dmapType != null)) {
			if (inputMap == null) {
				inputMap = new HashMap<String, String>();
			}
			inputMap.put(canonicalize(name), dmapType);
		}
	}
	
	/**
	 * Check whether a particular slot of a CAS annotation mention should be included
	 * in the DMAP Protege Frame used for the input token.
	 * <p>
	 * By default, no slots are included.
	 * 
	 * @param mention The CAS annotation mention name
	 * @param slot The CAS annotation mention slot name
	 * @return 'True' if this slot should be carried into the DMAP input token
	 */
	public boolean isInputMentionSlotIncluded(String mention, String slot) {
		if ((inputSlotMap == null) || (mention == null) || (slot == null)) {
			return false;
		} else {
			String ccls = canonicalize(mention);
			if (!inputSlotMap.containsKey(ccls)) return false;
			HashMap<String, String> slotMap = inputSlotMap.get(ccls);
			if (slotMap == null) return false;
			if (!slotMap.containsKey(slot)) return false;
			String dmapName = slotMap.get(slot);
			return (dmapName != null);
		}
	}
	
	/**
	 * Get the slot name to use in the DMAP reference built to represent this CAS
	 * annotation mention slot.
	 * 
	 * @param mention The CAS annotation mention name
	 * @param slot The CAS annotation mention slot name
	 * @return The DMAP reference slot name to use in the input token for this CAS mention
	 */
	public String getInputMentionSlotReferenceType(String mention, String slot) {
		if ((mention == null) || (slot == null)) return null;
		String ccls = canonicalize(mention);
		if (inputSlotMap == null) return slot;
		if (!inputSlotMap.containsKey(ccls)) return slot;
		HashMap<String, String> slotMap = inputSlotMap.get(ccls);
		if (slotMap == null) return slot;
		String dmapName = slotMap.get(slot);
		if (dmapName == null) return slot;
		return dmapName;
	}
	
	/**
	 * Set whether or not a CAS annotation mention slot should be included in
	 * DMAP input token references.
	 * 
	 * @param mentionThe CAS annotation mention name.
	 * @param slot The CAS annotation mention slot name.
	 * @param dmapSlot The DMAP reference name for the corresponding slot.  Or null if this slot should be excluded.
	 */
	public void setInputMentionSlotReferenceType(String mention, String slot, String dmapSlot) {
		if ((mention != null) && (slot != null)) {
			// Make sure there is a place for this entry
			if (inputSlotMap == null) 
				inputSlotMap = new HashMap<String, HashMap<String, String>>();
			String ccls = canonicalize(mention);
			if (!inputSlotMap.containsKey(ccls)) 
				inputSlotMap.put(ccls, new HashMap<String, String>());
			// Get the slot map for this mention type
			HashMap<String, String> slotMap = inputSlotMap.get(canonicalize(mention));
			slotMap.put(slot, null);
		}
	}
	
	/**
	 * Check whether any DMAP output references are to be written back into the CAS.
	 * 
	 * @return 'True' if there is at least one output reference of interest.
	 */
	public boolean hasOutputReferencesOfInterest() {
		return ((outputMap != null) && (outputMap.size() > 0));
	}
	
	/**
	 * Check whether a specific DMAP output reference type should be written
	 * back into the CAS.
	 * 
	 * @param name The name of the DMAP Reference frame type
	 * @return 'True' if this reference should be written back
	 */
	public boolean isOutputReferenceOfInterest(String name) {
		if ((outputMap == null) || (name == null)) {
			return false;
		} else {
			return outputMap.containsKey(canonicalize(name));
		}
	}
	
	/**
	 * Check whether a specific DMAP output reference should be written
	 * back into the CAS.
	 * 
	 * @param reference The reference to check
	 * @return 'True' if this reference should be written into the CAS
	 */
	public boolean isOutputReferenceOfInterest(Reference reference) {
		if ((referencesOfInterest == null) || (reference == null)) {
			return false;
		} else {
			for (String cls: referencesOfInterest) {
				if (reference.getItem().isa(cls)) return true;
			}
			return false;
		}
	}
	
	/**
	 * Get the type of CAS annotation mention that should be created to
	 * represent this DMAP reference.
	 * 
	 * @param reference The reference to check
	 * @return The type of CAS annotation mention to create
	 */
	public String getOutputReferenceMentionType(Reference reference) {
		if (reference == null) return null;
		String dmapType = reference.getText();
		if ((referencesOfInterest == null) || (outputMap == null)) {
			return dmapType;
		} else {
			for (String cls: referencesOfInterest) {
				if (reference.getItem().isa(cls)) {
					//try to map current Reference type first (most specific)
					String ccls = canonicalize(reference.getText());
					if (outputMap.containsKey(ccls)) {
						return outputMap.get(ccls);
					} else {
						// fallback is the map the cls name we matched
						ccls = canonicalize(cls);
						if (outputMap.containsKey(ccls)) {
							return outputMap.get(ccls);
						} else {
							return dmapType;
						}
					}
				}
			}
			return dmapType;
		}
	}
	
	/**
	 * Check whether this reference slot should be included in the
	 * annotation mention written back to the CAS.
	 * <p>
	 * By default, all slots are included (the opposite of input
	 * annotation to reference mappings).
	 * 
	 * @param reference The reference to be written back
	 * @param slot The name of the reference slot
	 * @return 'True' if this slot should be included in the CAS mention
	 */
	public boolean isOutputReferenceSlotOfInterest(Reference reference, DMAPItem slot) {
		if ((referencesOfInterest == null) || (reference == null) || (slot == null)) {
			return false;
		} else {
			for (String cls: referencesOfInterest) {
				if (reference.getItem().isa(cls)) {
					String ccls = canonicalize(cls);
					if (!outputSlotMap.containsKey(ccls)) return true;
					HashMap<String, String> slotMap = outputSlotMap.get(ccls);
					if (slotMap == null) return true;
					for (String key: slotMap.keySet()) {
						if (slot.isa(key)) {
							String mention = slotMap.get(key);
							return (mention != null);
						}
					}
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Get the name to use for this reference slot in the CAS annotation mention
	 * being written back to the CAS.
	 * 
	 * @param reference The reference being written back
	 * @param slot The name of the reference slot
	 * @return The name to use for the corresponding CAS slot
	 */
	public String getOutputReferenceSlotMentionType(Reference reference, DMAPItem slot) {
		if (slot == null) return null;
		String slotName = slot.getText();
		if ((referencesOfInterest == null) || (reference == null)) {
			return slotName;
		} else {
			for (String cls: referencesOfInterest) {
				if (reference.getItem().isa(cls)) {
					String ccls = canonicalize(cls);
					if (!outputSlotMap.containsKey(ccls)) return slotName;
					HashMap<String, String> slotMap = outputSlotMap.get(ccls);
					if (slotMap == null) return slotName;
					for (String key: slotMap.keySet()) {
						if (slot.isa(key)) {
							String mention = slotMap.get(key);
							if (mention == null) {
								return slotName;
							} else {
								return mention;
							}
						}
					}
					return slotName;
				}
			}
			return slotName;
		}		
	}
	
	/**
	 * Get the slot filler designated as the root for this reference.  If there
	 * is no designated slot, then just return the reference.
	 * <p>
	 * The root of a reference is the slot value that anchors the reference in
	 * the parsed text and disambiguates the reference from other references of
	 * the same type that were found.  For example, a slot that holds the "verb" for
	 * a reference might be a natural root.
	 * 
	 * @param reference The reference to find the root of.
	 * @return The root for that reference
	 */
	public Reference getOutputReferenceRoot(Reference reference) {
		if ((referencesOfInterest == null) || (reference == null)) {
			return reference;
		} else {
			for (String cls: referencesOfInterest) {
				if (reference.getItem().isa(cls)) {
					String ccls = canonicalize(cls);
					if (!referenceRoots.containsKey(ccls)) return reference;
					String rootSlotName = referenceRoots.get(ccls);
					if (rootSlotName == null) return reference;
					Reference root = reference.getSlotValue(rootSlotName);
					if (root == null) return reference;
					return root;
				}
			}
			return reference;
		}
	}
	
	/**
	 * Get all of the types of the references of interest.
	 * 
	 * @return The names of the reference types of interest.
	 */
	public Vector<String> getReferencesOfInterest() {
		return referencesOfInterest;
	}
	
	/**
	 * Get the type of CAS annotation mention that should be created to
	 * represent this DMAP reference type.
	 * 
	 * @param name The name of the DMAP reference type of interest.
	 * @return The corresponding CAS annotation mention type to create.
	 */
	public String getOutputReferenceMentionType(String name) {
		if ((outputMap == null) || (name == null)) {
			return null;
		} else {
			return outputMap.get(canonicalize(name));
		}		
	}
	
	/**
	 * Set the type of CAS annotation mention that should be created to
	 * represent this DMAP reference type.
	 * 
	 * @param dmapType The DMAP reference type of interest.
	 * @param mention The CAS annotation mention type to create for this DMAP type.
	 */
	public void setOutputReferenceMentionType(String dmapType, String mention) {
		if ((mention != null) && (dmapType != null)) {
			if (outputMap == null) {
				outputMap = new HashMap<String, String>();
			}
			String name = canonicalize(dmapType);
			outputMap.put(name, canonicalize(mention));
			if (referencesOfInterest == null) {
				referencesOfInterest = new Vector<String>();
			}
			if (!referencesOfInterest.contains(name)) referencesOfInterest.add(dmapType);
		}
	}
	
	/**
	 * Get the name of the slot holding the root reference for references of
	 * this DMAP type.
	 * 
	 * @param cls The type of the DMAP reference.
	 * @return The name of the DMAP reference slot holding the root for this type of reference.
	 */
	public String getOutputReferenceRootSlot(String cls) {
		if ((referenceRoots == null) || (cls == null)) {
			return null;
		} else {
			return referenceRoots.get(canonicalize(cls));
		}
	}
	
	/**
	 * Set the name of the slot holding the root reference for references of
	 * this DMAP type.
	 * 
	 * @param cls The type of the DMAP reference.
	 * @param root The name of the DMAP reference slot holding the root for this type of reference.
	 */
	public void setOutputReferenceRootSlot(String cls, String root) {
		if ((cls != null)) {
			if (root != null) {
				if (referenceRoots == null) 
					referenceRoots = new HashMap<String, String>();
				referenceRoots.put(canonicalize(cls), root);
			} else {
				if (referenceRoots != null) {
					referenceRoots.remove(canonicalize(cls));
				}
			}
		}
	}
	
	/**
	 * Check whether an output reference slot should be included in the CAS
	 * annotation mention being written back to the CAS.
	 * 
	 * @param cls The DMAP reference type
	 * @param slot The DMAP reference slot name
	 * @return 'True' if this slot should be excluded from the CAS
	 */
	public boolean isOutputReferenceSlotExcluded(String cls, String slot) {
		if ((outputSlotMap == null) || (cls == null) || (slot == null)) {
			return false;
		} else {
			String ccls = canonicalize(cls);
			if (!outputSlotMap.containsKey(ccls)) return false;
			HashMap<String, String> slotMap = outputSlotMap.get(ccls);
			if (slotMap == null) return false;
			if (!slotMap.containsKey(slot)) return false;
			String mention = slotMap.get(slot);
			return (mention == null);
		}
	}
	
	/**
	 * Set whether an output reference slot should be included in the CAS
	 * annotation mention being written back to the CAS.
	 * 
	 * @param cls The DMAP reference type
	 * @param slot The DMAP reference slot name
	 * @param excluded 'True' if this slot should be excluded from the CAS
	 */
	public void setOutputReferenceSlotExcluded(String cls, String slot, boolean excluded) {
		if ((cls != null) && (slot != null)) {
			if (excluded) {
				if (outputSlotMap == null) 
					outputSlotMap = new HashMap<String, HashMap<String, String>>();
				String ccls = canonicalize(cls);
				if (!outputSlotMap.containsKey(ccls)) 
					outputSlotMap.put(ccls, new HashMap<String, String>());
				outputSlotMap.put(slot, null);
			} else {
				if (outputSlotMap != null) {
					HashMap<String, String> slotMap = outputSlotMap.get(canonicalize(cls));
					if ((slotMap != null) && slotMap.containsKey(slot)) {
						String mention = slotMap.get(slot);
						if (mention == null) slotMap.remove(slot);
					}
				}
			}
		}
	}
	
	/**
	 * Read an annotation map configuration file into this annotation map.
	 * 
	 * @param file The file to load.
	 * @return The XML DOM document representing the contents of the configuration file.
	 * @throws ParserConfigurationException If there is no valid XML DOM parser.
	 * @throws SAXException If there is a syntax error in the configuration file XML.
	 * @throws IOException If there is a problem reading the file.
	 */
	private Document readConfigurationFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}
	
	/**
	 * Populate the input annotation map from entries in the configuration file.
	 * A mention specification is treated as a regular expression if there is no 
	 * corresponding frame explicitly given.
	 * 
	 * @param nodes The XML DOM nodes holding input annotation information.
	 * @param file The file holding the definition of these nodes.
	 */
	private void buildInputAnnotationMap(NodeList nodes, File file) {
		inputMap = new HashMap<String, String>();
		inputRegEx = new Vector<Pattern>();
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				String annotationType = getAttributeValue(node, "mention");
				String dmapType = getAttributeValue(node, "frame");
				if ((annotationType != null) && (dmapType != null)) {
					inputMap.put(canonicalize(annotationType), dmapType);
				} else {
					if ( annotationType != null ) {
						System.err.println("WARNING: input-annotation missing 'frame' attribute in " + file.getAbsolutePath() + ", 'mention'" + annotationType + "treated as RegExp.");
						Pattern mentionPattern = Pattern.compile(annotationType);
						inputRegEx.add(mentionPattern); // Note: we do not deal with slots in this case, so slots on the input annotation are ignored.
					} else {
						System.err.println("WARNING: input-annotation missing 'mention' and 'frame' attribute in " + file.getAbsolutePath());
					}
				}
				// The slot mappings
				NodeList slots = ((Element)node).getElementsByTagName("slot");
				if ((slots != null) && (slots.getLength() > 0)) {
					if (inputSlotMap == null) inputSlotMap = new HashMap<String, HashMap<String, String>>();
					// Get the slot map for this input annotation
					HashMap<String, String> slotMap = null;
					dmapType = canonicalize(dmapType);
					if (inputSlotMap.containsKey(dmapType)) {
						slotMap = inputSlotMap.get(dmapType);
					} else {
						slotMap = new HashMap<String, String>();
						inputSlotMap.put(dmapType, slotMap);
					}
					for (int j=0; j<slots.getLength(); j++) {
						// Get the slot attributes
						Node slot = slots.item(j);
						String name = getAttributeValue(slot, "name");
						String mention = getAttributeValue(slot, "mention");
						String excludeValue = getAttributeValue(slot, "exclude");
						if ((mention != null)) {
							// Add this slot to the various maps
							boolean exclude = false;
							if (excludeValue != null) {
								exclude = Boolean.parseBoolean(excludeValue);
							}
							if (exclude) {
								slotMap.put(mention, null);
							} else if (name != null) {
								slotMap.put(mention, name);
							} else {
								slotMap.put(mention, mention);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Populate the output annotation map from entries in the configuration file.
	 * 
	 * @param nodes The XML DOM nodes holding output annotation information.
	 * @param file The file holding the definition of these nodes.
	 */
	private void buildOutputAnnotationMap(NodeList nodes, File file) {
		outputMap = new HashMap<String, String>();
		outputSlotMap = new HashMap<String, HashMap<String, String>>();
		referenceRoots = new HashMap<String, String>();
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				// The attributes mapping from DMAP type to CAS type
				String annotationType = getAttributeValue(node, "mention");
				String dmapType = getAttributeValue(node, "frame");
				if ((annotationType != null) && (dmapType != null)) {
					setOutputReferenceMentionType(dmapType, annotationType);
				} else {
					System.err.println("WARNING: output-annotation missing 'mention' or 'frame' attribute in " + file.getAbsolutePath());
				}
				// The slot mappings
				NodeList slots = ((Element)node).getElementsByTagName("slot");
				if (slots != null) {
					// Get the slot map for this
					HashMap<String, String> slotMap = null;
					dmapType = canonicalize(dmapType);
					if (outputSlotMap.containsKey(dmapType)) {
						slotMap = outputSlotMap.get(dmapType);
					} else {
						slotMap = new HashMap<String, String>();
						outputSlotMap.put(dmapType, slotMap);
					}
					for (int j=0; j<slots.getLength(); j++) {
						// Get the slot attributes
						Node slot = slots.item(j);
						String name = getAttributeValue(slot, "name");
						String mention = getAttributeValue(slot, "mention");
						String rootValue = getAttributeValue(slot, "root");
						String excludeValue = getAttributeValue(slot, "exclude");
						if ((name != null)) {
							// Add this slot to the various maps
							boolean exclude = false;
							if (excludeValue != null) {
								exclude = Boolean.parseBoolean(excludeValue);
							}
							if (exclude) {
								slotMap.put(name, null);
							} else if (mention != null) {
								slotMap.put(name, mention);
							} else {
								slotMap.remove(name);
							}
							boolean root = false;
							if (rootValue != null) {
								root = Boolean.parseBoolean(rootValue);
							}
							if (root) {
								referenceRoots.put(dmapType, name);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get the value of an attribute from a DOM element.
	 * 
	 * @param node The DOM element.
	 * @param name The name of the attribute.
	 * @return The attribute value.
	 */
	private String getAttributeValue(Node node, String name) {
		if (node.hasAttributes()) {
			NamedNodeMap attribs = node.getAttributes();
			Node value = attribs.getNamedItem(name);
			if (value != null) {
				return getNodeText(value);
			}
		}
		return null;
	}
	
	/**
	 * Get the body text of a DOM element.
	 * 
	 * @param node The DOM element.
	 * @return The body text from the element.
	 */
	private String getNodeText(Node node) {
		if (node.getNodeType() == Node.TEXT_NODE) {
			return node.getNodeValue();
		} else {
			NodeList children = node.getChildNodes();
			if (children.getLength() > 0) {
				StringBuffer sb = new StringBuffer();
				for (int i=0; i<children.getLength(); i++) {
					String value = getNodeText(children.item(i));
					if ((value != null) && !value.equals("")) {
						sb.append(value);
					}
				}
				if (sb.length() > 0) {
					return sb.toString();
				}
			}
		}
		return null;
	}
	
	/**
	 * Put a string into a standard form to use as a hash key.
	 * 
	 * @param thing The string to standardize.
	 * @return The standardized version of the string.
	 */
	private String canonicalize(String thing) {
		if (thing == null) return null;
		return thing.trim().toLowerCase();
	}
	
	/**
	 * Print out the contents of this annotation map in a human-readable format.
	 * 
	 * @param stream The stream to print to.
	 * @param indent An indent amount.
	 */
	public void print(PrintStream stream, int indent) {
		printString(stream, indent, "Input Annotation Map");
		if ((inputMap == null) || inputMap.isEmpty()) {
			printString(stream, indent+2, "Empty");
		} else {
			for (String key: inputMap.keySet()) {
				printString(stream, indent+2, key + " -> " + inputMap.get(key));
			}
		}
		
		printString(stream, indent, "Input Regular Expressions");
		if (( inputRegEx == null) || (inputRegEx.isEmpty())) {
			printString(stream, indent+2, "Empty");			
		} else {
			for ( Pattern pat: inputRegEx) {
				printString(stream, indent+2, pat.pattern());
			}
		}
		
		printString(stream, indent, "Output Annotation Map");
		if ((outputMap == null) || outputMap.isEmpty()) {
			printString(stream, indent+2, "Empty");
		} else {
			for (String key: outputMap.keySet()) {
				printString(stream, indent+2, key + " -> " + outputMap.get(key));
				if (outputSlotMap != null) {
					String root = null;
					if ((referenceRoots != null) && referenceRoots.containsKey(key)) root = referenceRoots.get(key);
					HashMap<String, String> slotMap = outputSlotMap.get(key);
					boolean sawRoot = false;
					if ((slotMap != null)) {
						for (String slot: slotMap.keySet()) {
							printIndent(stream, indent+4);
							stream.print(slot);
							String mention = slotMap.get(slot);
							if (mention != null) {
								stream.print(" -> ");
								stream.print(mention);
							}
							if (slot.equalsIgnoreCase(root)) {
								stream.print(", root");
								sawRoot = true;
							}
							if (mention == null) stream.print(", exclude");
							stream.println();
						}
					}
					if ((root != null) && !sawRoot) {
						printString(stream, indent+4, root + ", root");
					}
				}
			}
		}
	}
	
	/**
	 * Print an indent.
	 * 
	 * @param stream The stream to indent.
	 * @param indent The indent amount.
	 */
  private void printIndent(PrintStream stream, int indent) {
		for (int i=0; i<indent; i++) stream.print(" ");
  }
	
  /**
   * Print a string with a specified indent.
   * 
   * @param stream The stream to print to.
   * @param indent The indent amount.
   * @param text The string to print with the indent.
   */
	private void printString(PrintStream stream, int indent, String text) {
		printIndent(stream, indent);
		stream.println(text);
	}
	
}
