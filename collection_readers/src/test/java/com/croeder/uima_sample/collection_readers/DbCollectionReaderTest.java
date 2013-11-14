package com.croeder.uima_sample.collection_readers;


import static  org.junit.Assert.assertEquals;
import static  org.junit.Assert.assertTrue;
import org.junit.Test;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.JCasUtil;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import com.croeder.uima_sample.relational_document_index.DocumentProviderType;


public class DbCollectionReaderTest {

	

	@Test
	public void testCollectionReader() throws Exception {
		String[] typeSystemDescriptions = { "com.croeder.uima_sample.TypeSystem" };
		TypeSystemDescription tsd 
			= TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemDescriptions); 
		CollectionReader cr = CollectionReaderFactory.createCollectionReader(
			DbCollectionReader.class, tsd,
			DbCollectionReader.PARAM_BATCH_NUMBER, 1,
			DbCollectionReader.PARAM_COLLECTION_TYPE, DocumentProviderType.Medline.toString());


		JCasIterable iter = new JCasIterable(cr);
		assertTrue(iter.hasNext());
		JCas jcas = iter.next();
		assertEquals("The effect of propranolol, phentolamine, papaverine, theophyline and Ca++, administered in different combinations of their threshold doses, on the relaxing effect of adrenaline was studied on an isolated segment of proximal jejunum of male cats. It was established that phentolamine weakened the relaxing effect of adrenaline, while propranolol had no effect on it. Papaverine potentiated the relaxinf effects of adrenaline both when administered alone and in combination with propranolol or with phentolamine. Theophylline weakened the relaxing effect of adfrenaline and of the combination phentolamine-adrenaline. Ca++ increased the smooth-muscle tone. The interpretation of the results obtained leads to the fundamental conclusions that the relaxing effect of adrenaline on cat jejunum is more alpha- than beta-adrenergically determined and that the system of the cyclic AMP participates in its realization. At the smae time, however, the possibility of participation of other mechanisms is not excluded. The smooth-muscle effect of papaverine and theophylline is not determined only by their inhibitory effect on phosphodiesterase.", jcas.getDocumentText());
	}

}
