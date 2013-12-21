/*
 Copyright (c) 2013, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.tsx;


import java.util.Collection;
import java.util.ArrayList;

import org.apache.uima.jcas.JCas;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.ts.SlotMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;
import edu.ucdenver.ccp.nlp.ts.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.ts.ComplexSlotMention;


/**
 *  static functions that extend the generated ClassMention class 
 **/
public class ClassMentionX {


	private static Collection<SlotMention> getSlotMentionsByType(ClassMention cm,Class clazz) {
		Collection<SlotMention> slotList = new ArrayList<SlotMention>();
        FSArray slotMentionsArray = cm.getSlotMentions();
        if (slotMentionsArray != null) {
            for (int i = 0; i < slotMentionsArray.size(); i++) {
                FeatureStructure fs = slotMentionsArray.get(i);
				SlotMention sm = (SlotMention) fs;
				if (clazz.isInstance(fs)) {
                	slotList.add(sm);
				}
            }
		}
		return slotList;
	}

	public static Collection<String> getSlotMentionNames(ClassMention cm) {
		Collection<String> nameList = new ArrayList<String>();

		for (SlotMention sm : getSlotMentionsByType(cm, SlotMention.class)) {
        	nameList.add(sm.getMentionName());
		}

		return nameList;
	}

	public static Collection<String> getPrimitiveSlotMentionNames(ClassMention cm) {
		Collection<String> nameList = new ArrayList<String>();

		for (SlotMention sm : getSlotMentionsByType(cm, PrimitiveSlotMention.class)) {
        	nameList.add(sm.getMentionName());
		}

		return nameList;
	}

	public static Collection<String> getComplexSlotMentionNames(ClassMention cm) {
		Collection<String> nameList = new ArrayList<String>();

		for (SlotMention sm : getSlotMentionsByType(cm, ComplexSlotMention.class)) {
        	nameList.add(sm.getMentionName());
		}

		return nameList;
	}


	/**
	 * Sets a single String value on the named slot, obliterating any
	 * previously present values, throwing if there was more than one.
	 * this will also throw if the named slot isn't of type StringSlotMention.
	 */
	public void setStringSlotMentionValue(JCas jCas, ClassMention cm, String name, String value) 
	throws Exception {

		StringSlotMention sm = (StringSlotMention) getPrimitiveSlotMentionByName(cm, name);
		if (sm == null) {		
			sm = new StringSlotMention(jCas);
			sm.setMentionName(name);
		}
		else {
			// TODO: throw if > 1 value here already
		}
		StringArray values = new StringArray(jCas, 1);
		values.set(0,value);
		sm.setSlotValues(values);

		FSArray slots = cm.getSlotMentions();
		FSArray newSlots = null;
		if (slots == null) {
			newSlots = new FSArray(jCas, 1);
			newSlots.set(0, sm);
		}	
		else {
			newSlots = new FSArray(jCas, slots.size());
			for (int i=0; i<slots.size(); i++) {
				newSlots.set(i, slots.get(i));
			}
			// TODO: check for name repeat
			newSlots.set(slots.size(), sm);
		}
		cm.setSlotMentions(newSlots);
	}

	public static String getStringSlotMentionValue(ClassMention cm, String name) {
		StringSlotMention slot = (StringSlotMention) getSlotMentionByName(cm, name);
		return slot.getSlotValues(0);
	}


	/**
	 * returns first SlotMention with given name, null if none.
	 **/
	public static SlotMention getSlotMentionByName(ClassMention cm, String name) {
		
		for (SlotMention sm : getSlotMentionsByType(cm, SlotMention.class)) {
        	if (name.equals(sm.getMentionName())) {
				return sm;
			}
		}

		return null;
    }

	public static PrimitiveSlotMention getPrimitiveSlotMentionByName(ClassMention cm, String name) {

		for (SlotMention sm : getSlotMentionsByType(cm, PrimitiveSlotMention.class)) {
        	if (name.equals(sm.getMentionName())) {
				return (PrimitiveSlotMention) sm;
			}
		}

		return null;
    }

	public static ComplexSlotMention getComplexSlotMentionByName(ClassMention cm, String name) {

		for (SlotMention sm : getSlotMentionsByType(cm, ComplexSlotMention.class)) {
        	if (name.equals(sm.getMentionName())) {
				return (ComplexSlotMention) sm;
			}
		}

		return null;
    }

	public static void addSlotValue(ClassMention classMention, String slotMentionName, String slotValue)
            throws CASException {
        JCas jcas = classMention.getCAS().getJCas();
        SlotMention slotMention = ClassMentionX.getSlotMentionByName(classMention, slotMentionName);
        if (slotMention != null) {
            if (slotMention instanceof StringSlotMention) {
                StringSlotMention ccpSSM = (StringSlotMention) slotMention;
                StringArray slotValues = ccpSSM.getSlotValues();
                slotValues = SlotMentionX.addToStringArray(slotValues, slotValue, jcas);
                ccpSSM.setSlotValues(slotValues);
            } else {
                throw new CASException(new RuntimeException("Cannot store a String in a " + slotMention.getClass().getName()));
            }
        } else {
            StringSlotMention ccpSSM = new StringSlotMention(jcas);
            ccpSSM.setMentionName(slotMentionName);
            addSlotMention(classMention, ccpSSM);
            StringArray slotValues = new StringArray(jcas, 1);
            slotValues.set(0, slotValue);
            ccpSSM.setSlotValues(slotValues);
        }

    }

    private static void addSlotMention(ClassMention ccpClassMention, SlotMention ccpSlotMention)
            throws CASException {
        FSArray slotMentions = ccpClassMention.getSlotMentions();
        if (slotMentions == null) {
            slotMentions = new FSArray(ccpClassMention.getCAS().getJCas(), 1);
            slotMentions.set(0, ccpSlotMention);
        } else {
            FeatureStructure[] featureStructures = slotMentions.toArray();
            int index = 0;
            slotMentions = new FSArray(ccpClassMention.getCAS().getJCas(), featureStructures.length + 1);
            for (FeatureStructure fs : featureStructures) {
                slotMentions.set(index++, fs);
            }
            slotMentions.set(index, ccpSlotMention);
        }
        ccpClassMention.setSlotMentions(slotMentions);
    }


}
