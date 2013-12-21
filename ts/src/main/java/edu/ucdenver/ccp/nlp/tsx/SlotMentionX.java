package edu.ucdenver.ccp.nlp.tsx;

import edu.ucdenver.ccp.nlp.ts.StringSlotMention;
import edu.ucdenver.ccp.nlp.ts.DoubleSlotMention;
import edu.ucdenver.ccp.nlp.ts.BooleanSlotMention;
import edu.ucdenver.ccp.nlp.ts.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.ts.FloatSlotMention;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.BooleanArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.FloatArray;
import org.apache.uima.jcas.JCas;

public class SlotMentionX {

	public static String getFirstSlotValue(StringSlotMention ssm) {
        StringArray slotArray = ssm.getSlotValues();
        if (slotArray != null && slotArray.size() > 0) {
            return slotArray.get(0);
        }
        return null;
    }
    public static Double getFirstSlotValue(DoubleSlotMention dsm) {
        DoubleArray slotArray = dsm.getSlotValues();
        if (slotArray != null && slotArray.size() > 0) {
            return slotArray.get(0);
        }
        return null;
    }
    public static boolean getFirstSlotValue(BooleanSlotMention bsm) {
        return bsm.getSlotValue();
    }

    public static Integer getFirstSlotValue(IntegerSlotMention ism) {
        IntegerArray slotArray = ism.getSlotValues();
        if (slotArray != null && slotArray.size() > 0) {
            return slotArray.get(0);
        }
        return null;
    }

    public static Float getFirstSlotValue(FloatSlotMention fsm) {
        FloatArray slotArray = fsm.getSlotValues();
        if (slotArray != null && slotArray.size() > 0) {
            return slotArray.get(0);
        }
        return null;
    }
    public static StringArray addToStringArray(StringArray stringArray, String stringToAdd, JCas jcas) {
        if (stringArray == null) {
            stringArray = new StringArray(jcas, 0);
        }
        StringArray stringArrayToReturn = new StringArray(jcas, stringArray.size() + 1);
        for (int i = 0; i < stringArray.size(); i++) {
            stringArrayToReturn.set(i, stringArray.get(i));
        }
        stringArrayToReturn.set(stringArray.size(), stringToAdd);
        return stringArrayToReturn;
    }
}
