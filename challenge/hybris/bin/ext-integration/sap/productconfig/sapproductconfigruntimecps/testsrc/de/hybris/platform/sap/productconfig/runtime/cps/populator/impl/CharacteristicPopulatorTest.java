/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("javadoc")
@UnitTest
public class CharacteristicPopulatorTest
{
	private static final String CONFIG_ID = "configId";
	private static final String ITEM_ID = "itemId";
	CharacteristicPopulator classUnderTest;
	private CPSCharacteristic source;
	private CsticModel target;
	private static final String csticId = "csticId";
	private CPSValue value;
	private static final String valueString = "VALUE";
	private static final String valueNotSelectable = "VALUE_NOT_SELECTABLE";
	private CPSPossibleValue possibleValue;
	private CPSPossibleValue possibleValueNotSelectable;
	private static final String kbId = "99";
	private CPSCharacteristic sourceIntervals;
	private CPSPossibleValue possibleValueInterval;
	private static final String intervalLow = "12";
	private static final String intervalHigh = "20";
	private List<CPSPossibleValue> possibleValues;
	private static final String ITEM_KEY = "PRODUCT_KEY";
	private static final String interValTypeClose = "3";

	@Mock
	private ContextualConverter<CPSValue, CsticValueModel, MasterDataContext> mockedValueConverter;
	@Mock
	private ContextualConverter<CPSPossibleValue, CsticValueModel, MasterDataContext> mockedPossibleValueConverter;
	@Mock
	private final CsticValueModel valueModel = new CsticValueModelImpl();

	private void checkOneValuePresentInTarget()
	{
		final List<CsticValueModel> values = target.getAssignedValues();
		assertNotNull(values);
		assertEquals(1, values.size());
	}

	private void checkOnePossibleValuePresent()
	{
		final List<CsticValueModel> values = target.getAssignableValues();
		assertNotNull(values);
		assertEquals(1, values.size());
	}


	Answer<CsticValueModel> valueModelAnswer = new Answer<CsticValueModel>()
	{
		public CsticValueModel answer(final InvocationOnMock invocation) throws Throwable
		{
			final CsticValueModel csticModelValue = new CsticValueModelImpl();
			csticModelValue.setName(valueString);
			return csticModelValue;
		}
	};

	private final CPSPossibleValue possibleValueUnconstrained = new CPSPossibleValue();
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private MasterDataContext ctxt;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);

		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);


		classUnderTest = new CharacteristicPopulator();

		final CPSConfiguration config = new CPSConfiguration();
		config.setId(CONFIG_ID);
		config.setKbId(kbId);
		final CPSItem item = new CPSItem();
		item.setId(ITEM_ID);
		item.setKey(ITEM_KEY);
		item.setParentConfiguration(config);
		source = new CPSCharacteristic();
		source.setParentItem(item);
		source.setPossibleValues(new ArrayList<>());
		source.setValues(new ArrayList<>());

		target = new CsticModelImpl();

		value = new CPSValue();
		value.setValue(valueString);
		value.setParentCharacteristic(source);

		possibleValue = new CPSPossibleValue();
		possibleValue.setValueLow(valueString);
		possibleValue.setSelectable(true);
		possibleValue.setParentCharacteristic(source);
		possibleValueNotSelectable = new CPSPossibleValue();
		possibleValueNotSelectable.setValueLow(valueNotSelectable);
		possibleValue.setParentCharacteristic(source);

		source.getValues().add(value);
		source.getPossibleValues().add(possibleValue);
		source.getPossibleValues().add(possibleValueNotSelectable);
		source.setId(csticId);
		source.setComplete(true);
		source.setConsistent(true);
		source.setReadOnly(false);
		source.setRequired(true);

		sourceIntervals = new CPSCharacteristic();
		sourceIntervals.setPossibleValues(new ArrayList<>());
		sourceIntervals.setValues(new ArrayList<>());
		possibleValueInterval = new CPSPossibleValue();
		possibleValueInterval.setValueLow(intervalLow);
		possibleValueInterval.setValueHigh(intervalHigh);
		possibleValueInterval.setIntervalType(interValTypeClose);
		possibleValueInterval.setSelectable(true);
		possibleValueInterval.setParentCharacteristic(sourceIntervals);
		sourceIntervals.getPossibleValues().add(possibleValueInterval);

		classUnderTest.setValueConverter(mockedValueConverter);
		Mockito.when(mockedValueConverter.convertWithContext(Mockito.any(), Mockito.any())).thenAnswer(valueModelAnswer);
		classUnderTest.setPossibleValueConverter(mockedPossibleValueConverter);
		Mockito.when(mockedPossibleValueConverter.convertWithContext(Mockito.any(), Mockito.any())).thenAnswer(valueModelAnswer);


		possibleValues = new ArrayList<CPSPossibleValue>();
		possibleValues.add(possibleValueInterval);

		possibleValueUnconstrained.setIntervalType(CharacteristicPopulator.INTERVAL_TYPE_UNCONSTRAINED);
	}

	@Test
	public void testPopulateCoreAttributes_Complete_Consistent_ReadOnly_Required()
	{
		source.setComplete(true);
		source.setConsistent(true);
		source.setReadOnly(false);
		source.setRequired(true);
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(Boolean.valueOf(source.isComplete()), Boolean.valueOf(target.isComplete()));
		assertEquals(Boolean.valueOf(source.isConsistent()), Boolean.valueOf(target.isConsistent()));
		assertEquals(Boolean.valueOf(source.isReadOnly()), Boolean.valueOf(target.isReadonly()));
		assertEquals(Boolean.valueOf(source.isRequired()), Boolean.valueOf(target.isRequired()));
	}

	@Test
	public void testValueConverter()
	{
		assertEquals(mockedValueConverter, classUnderTest.getValueConverter());
	}

	@Test
	public void testPossibleValueConverter()
	{
		assertEquals(mockedPossibleValueConverter, classUnderTest.getPossibleValueConverter());
	}

	@Test
	public void testPopulateCoreAttributesId()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(csticId, target.getName());
	}

	@Test
	public void testPopulateCoreAttributesStaticDomain()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(2, target.getStaticDomainLength());
		assertEquals(source.getPossibleValues().size(), target.getStaticDomainLength());
	}

	@Test
	public void testPopulateCoreAttributesVisibleFalse()
	{
		source.setVisible(false);
		classUnderTest.populateCoreAttributes(source, target);
		assertFalse("target visiblity should be \'false\': ", target.isVisible());
	}

	@Test
	public void testPopulateCoreAttributesVisibleTrue()
	{
		source.setVisible(true);
		classUnderTest.populateCoreAttributes(source, target);
		assertTrue("target visiblity should be \'true\': ", target.isVisible());
	}

	@Test
	public void testPopulateValues()
	{
		classUnderTest.populateValues(source, target, ctxt);
		checkOneValuePresentInTarget();
	}

	@Test
	public void testPopulateValuesNull()
	{
		source.setValues(null);
		classUnderTest.populateValues(source, target, ctxt);
		assertTrue(target.getAssignedValues().isEmpty());
	}

	@Test
	public void testPopulateValuesCsticIdPopulatedToValue()
	{
		classUnderTest.populateValues(source, target, ctxt);
		final CPSValue cloudEngineValue = source.getValues().get(0);
		assertNotNull(cloudEngineValue);
		assertEquals(csticId, cloudEngineValue.getParentCharacteristic().getId());
	}

	@Test
	public void testPopulateValuesKbIdPopulatedToValue()
	{
		classUnderTest.populateValues(source, target, ctxt);
		final CPSValue cloudEngineValue = source.getValues().get(0);
		assertNotNull(cloudEngineValue);
		assertEquals(kbId, cloudEngineValue.getParentCharacteristic().getParentItem().getParentConfiguration().getKbId());
	}

	@Test
	public void testPopulateValuesCheckOrder()
	{
		final CPSCharacteristic multiSource = new CPSCharacteristic();
		multiSource.setComplete(true);
		multiSource.setConsistent(true);
		multiSource.setId("OPTIONS");
		multiSource.setReadOnly(false);
		multiSource.setRequired(false);
		multiSource.setVisible(true);

		final List<CPSPossibleValue> cpsPossibleValues = new ArrayList<>();
		cpsPossibleValues.add(createPossibleValue(multiSource, "V1"));
		cpsPossibleValues.add(createPossibleValue(multiSource, "V2"));
		cpsPossibleValues.add(createPossibleValue(multiSource, "V3"));
		cpsPossibleValues.add(createPossibleValue(multiSource, "V4"));
		multiSource.setPossibleValues(cpsPossibleValues);

		final List<CPSValue> cpsValues = new ArrayList<>();
		cpsValues.add(createValue(multiSource, "V4"));
		cpsValues.add(createValue(multiSource, "V1"));
		cpsValues.add(createValue(multiSource, "V2"));
		multiSource.setValues(cpsValues);

		Mockito.when(mockedValueConverter.convertWithContext(Mockito.any(), Mockito.any())).thenAnswer(valueModelAnswerUsingInput);

		classUnderTest.populateValues(multiSource, target, ctxt);
		assertEquals(3, target.getAssignedValues().size());
		final List<CsticValueModel> values = target.getAssignedValues();
		assertEquals("V1", values.get(0).getName());
		assertEquals("V2", values.get(1).getName());
		assertEquals("V4", values.get(2).getName());
	}

	@Test
	public void testPopulateValuesWithAdditionalValuesCheckOrder()
	{
		final CPSCharacteristic multiSource = new CPSCharacteristic();
		multiSource.setComplete(true);
		multiSource.setConsistent(true);
		multiSource.setId("OPTIONS");
		multiSource.setReadOnly(false);
		multiSource.setRequired(false);
		multiSource.setVisible(true);

		final List<CPSPossibleValue> cpsPossibleValues = new ArrayList<>();
		cpsPossibleValues.add(createPossibleValue(multiSource, "V1"));
		cpsPossibleValues.add(createPossibleValue(multiSource, "V2"));
		cpsPossibleValues.add(createPossibleValue(multiSource, "V3"));
		cpsPossibleValues.add(createPossibleValue(multiSource, "V4"));
		multiSource.setPossibleValues(cpsPossibleValues);

		final List<CPSValue> cpsValues = new ArrayList<>();
		cpsValues.add(createValue(multiSource, "V4"));
		cpsValues.add(createValue(multiSource, "V1"));
		cpsValues.add(createValue(multiSource, "V5"));
		cpsValues.add(createValue(multiSource, "V2"));
		multiSource.setValues(cpsValues);

		Mockito.when(mockedValueConverter.convertWithContext(Mockito.any(), Mockito.any())).thenAnswer(valueModelAnswerUsingInput);

		classUnderTest.populateValues(multiSource, target, ctxt);
		assertEquals(4, target.getAssignedValues().size());
		final List<CsticValueModel> values = target.getAssignedValues();
		assertEquals("V1", values.get(0).getName());
		assertEquals("V2", values.get(1).getName());
		assertEquals("V4", values.get(2).getName());
		assertEquals("V5", values.get(3).getName());
	}

	Answer<CsticValueModel> valueModelAnswerUsingInput = new Answer<CsticValueModel>()
	{
		public CsticValueModel answer(final InvocationOnMock invocation) throws Throwable
		{
			final CPSValue cpsValue = (CPSValue) invocation.getArguments()[0];
			final CsticValueModel csticModelValue = new CsticValueModelImpl();
			csticModelValue.setName(cpsValue.getValue());
			return csticModelValue;
		}
	};

	private CPSValue createValue(final CPSCharacteristic multiSource, final String value)
	{
		final CPSValue cpsValue = new CPSValue();

		cpsValue.setAuthor("User");
		cpsValue.setParentCharacteristic(multiSource);
		cpsValue.setSelected(false);
		cpsValue.setValue(value);

		return cpsValue;
	}

	private CPSPossibleValue createPossibleValue(final CPSCharacteristic multiSource, final String value)
	{
		final CPSPossibleValue cpsPossibleValue = new CPSPossibleValue();

		cpsPossibleValue.setIntervalType("1");
		cpsPossibleValue.setParentCharacteristic(multiSource);
		cpsPossibleValue.setSelectable(true);
		cpsPossibleValue.setValueLow(value);

		return cpsPossibleValue;
	}


	@Test
	public void testPossibleValuesCsticIdPopulatedToPossibleValue()
	{
		classUnderTest.populatePossibleValues(source, target, ctxt);
		final CPSPossibleValue cloudEnginePossibleValue = source.getPossibleValues().get(0);
		assertEquals(csticId, cloudEnginePossibleValue.getParentCharacteristic().getId());
	}

	@Test
	public void testPossibleValuesSetParent()
	{
		final CPSPossibleValue cloudEnginePossibleValue = source.getPossibleValues().get(0);
		classUnderTest.populatePossibleValues(source, target, ctxt);
		assertEquals(source, cloudEnginePossibleValue.getParentCharacteristic());
	}

	@Test
	public void testPossibleValuesKbIdPopulatedToPossibleValue()
	{
		classUnderTest.populatePossibleValues(source, target, ctxt);
		final CPSPossibleValue cloudEnginePossibleValue = source.getPossibleValues().get(0);
		assertEquals(kbId, cloudEnginePossibleValue.getParentCharacteristic().getParentItem().getParentConfiguration().getKbId());
	}

	@Test
	public void testPopulateValuesCalledDuringPopulate()
	{
		classUnderTest.populate(source, target, ctxt);
		checkOneValuePresentInTarget();
	}

	@Test
	public void testPopulatePossibleValues()
	{
		classUnderTest.populatePossibleValues(source, target, ctxt);
		checkOnePossibleValuePresent();
	}

	@Test
	public void testPopulatePossibleValuesNull()
	{
		source.setPossibleValues(null);
		classUnderTest.populatePossibleValues(source, target, ctxt);
		assertTrue(target.getAssignableValues().isEmpty());
		assertFalse(target.isIntervalInDomain());
	}

	@Test
	public void testPopulatePossibleValuesCalledDuringPopulate()
	{
		classUnderTest.populate(source, target, ctxt);
		checkOnePossibleValuePresent();
	}

	@Test
	public void testPopulatePossibleValuesPlaceHolderEmtpty()
	{
		classUnderTest.populatePossibleValues(source, target, ctxt);
		//placeholder is set in facade
		assertNull(target.getPlaceholder());
	}

	@Test
	public void testPopulateNumericCsticWithIntervals()
	{
		classUnderTest.populatePossibleValues(sourceIntervals, target, ctxt);
		assertTrue(target.isIntervalInDomain());
	}


	@Test
	public void testPopulateNumericCsticWithIntervalsNoInterval()
	{
		possibleValueInterval.setValueHigh(null);
		possibleValueInterval.setIntervalType(null);
		classUnderTest.populatePossibleValues(sourceIntervals, target, ctxt);
		assertFalse(target.isIntervalInDomain());
	}

	@Test
	public void testPopulateInstanceReference()
	{
		classUnderTest.populateInstanceReference(source, target);
		assertEquals(ITEM_KEY, target.getInstanceName());
		assertEquals(ITEM_ID, target.getInstanceId());

	}

	@Test(expected = IllegalStateException.class)
	public void testPopulateInstanceReferenceNoParent()
	{
		source.setParentItem(null);
		classUnderTest.populateInstanceReference(source, target);
	}

	@Test
	public void testIsConstrainedNullPossibleValues()
	{
		source.setPossibleValues(null);
		assertFalse(classUnderTest.isConstrained(source));
	}

	@Test
	public void testIsConstrainedEmptyPossibleValues()
	{
		source.setPossibleValues(Collections.emptyList());
		assertFalse(classUnderTest.isConstrained(source));
	}

	@Test
	public void testIsConstrained2AllowsAdditionalValue()
	{
		final List<CPSPossibleValue> list2Entries = new ArrayList<>();
		list2Entries.add(possibleValueUnconstrained);
		list2Entries.add(possibleValueUnconstrained);
		source.setPossibleValues(list2Entries);
		assertTrue(classUnderTest.isConstrained(source));
	}

	@Test
	public void testIsConstrainedAllowsAdditionalValue()
	{
		final List<CPSPossibleValue> list2Entries = new ArrayList<>();
		list2Entries.add(possibleValueUnconstrained);
		source.setPossibleValues(list2Entries);
		assertFalse(classUnderTest.isConstrained(source));
	}


	@Test
	public void testIsConstrainedOnlyOneRegularPossiblenValue()
	{
		final List<CPSPossibleValue> list2Entries = new ArrayList<>();
		list2Entries.add(possibleValueUnconstrained);
		possibleValueUnconstrained.setIntervalType("1");
		source.setPossibleValues(list2Entries);
		assertTrue(classUnderTest.isConstrained(source));
	}

	@Test
	public void testAllowsAdditionalValue()
	{
		final List<CPSPossibleValue> list2Entries = new ArrayList<>();
		list2Entries.add(possibleValueUnconstrained);
		source.setPossibleValues(list2Entries);
		assertTrue(classUnderTest.allowsAdditionalValue(source));
	}

	@Test
	public void testAllowsAdditionalValueNoValue()
	{
		source.setPossibleValues(Collections.emptyList());
		assertFalse(classUnderTest.allowsAdditionalValue(source));
	}

	@Test
	public void testAllowsAdditionalValueNullValue()
	{
		source.setPossibleValues(null);
		assertFalse(classUnderTest.allowsAdditionalValue(source));
	}

	@Test
	public void testAllowsAdditionalValueOneRegularPossiblealue()
	{
		final List<CPSPossibleValue> list2Entries = new ArrayList<>();
		list2Entries.add(possibleValueUnconstrained);
		possibleValueUnconstrained.setIntervalType("1");
		source.setPossibleValues(list2Entries);
		assertFalse(classUnderTest.allowsAdditionalValue(source));
	}

	@Test
	public void testPopulatePossibleValuesIgnoreIntrevalTypeUnconstrained()
	{
		source.getPossibleValues().get(0).setIntervalType(CharacteristicPopulator.INTERVAL_TYPE_UNCONSTRAINED);
		classUnderTest.populate(source, target, ctxt);
		assertNotNull(target.getAssignableValues());
		assertTrue(target.getAssignableValues().size() == 1);
	}

	@Test
	public void testWithoutAdditionalValues()
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = new CsticValueModelImpl();
		assignedValue.setName(valueString);
		assignedValues.add(assignedValue);
		target.setAssignedValues(assignedValues);

		classUnderTest.populatePossibleValues(source, target, ctxt);
		assertNotNull(target.getAssignableValues());
		assertTrue(target.getAssignableValues().size() == 1);
		assertTrue(target.getAssignableValues().get(0).getName().equals(valueString));
		assertTrue(target.getAssignedValues().get(0).getName().equals(valueString));
	}

	@Test
	public void testAdditionalValues()
	{
		final String additionalValue = "ADDITIONAL_VALUE";
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = new CsticValueModelImpl();
		assignedValue.setName(additionalValue);
		assignedValues.add(assignedValue);
		target.setAssignedValues(assignedValues);

		classUnderTest.populatePossibleValues(source, target, ctxt);
		assertNotNull(target.getAssignableValues());
		assertTrue(target.getAssignableValues().size() == 2);
		assertTrue(target.getAssignableValues().get(1).getName().equals(additionalValue));
		assertTrue(target.getAssignedValues().get(0).getName().equals(additionalValue));
	}

	@Test
	public void testMergeAssignedWithPossibleValues_constarined()
	{
		assertTrue(
				classUnderTest.mergeAssignedWithPossibleValues(true, false, Collections.singletonList(new CsticValueModelImpl())));
	}

	@Test
	public void testMergeAssignedWithPossibleValues_additionalValue()
	{
		assertTrue(
				classUnderTest.mergeAssignedWithPossibleValues(false, true, Collections.singletonList(new CsticValueModelImpl())));
	}

	@Test
	public void testMergeAssignedWithPossibleValues_simpleInputWithDefault()
	{
		assertFalse(
				classUnderTest.mergeAssignedWithPossibleValues(false, false, Collections.singletonList(new CsticValueModelImpl())));
	}

	@Test
	public void testMergeAssignedWithPossibleValues_nothingAssigned()
	{
		assertFalse(classUnderTest.mergeAssignedWithPossibleValues(true, true, null));
	}
}
