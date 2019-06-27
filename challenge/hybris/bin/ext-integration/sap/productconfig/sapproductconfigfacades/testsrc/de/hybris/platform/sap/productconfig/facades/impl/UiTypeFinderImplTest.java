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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.sap.productconfig.facades.CPQImageType;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class UiTypeFinderImplTest
{
	private final UiTypeFinderImpl classUnderTest = new UiTypeFinderImpl();

	private CsticModel csticModel;

	@Before
	public void setup()
	{
		csticModel = new CsticModelImpl();
	}

	protected CsticModel createSimpleInput()
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(createAssignableValueList(0));
		csticModel.setEntryFieldMask(null);
		csticModel.setMultivalued(false);
		csticModel.setReadonly(false);
		csticModel.setValueType(CsticModel.TYPE_STRING);
		return csticModel;
	}

	protected CsticModel createNumericInput(final int valueType)
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(createAssignableValueList(0));
		csticModel.setEntryFieldMask(null);
		csticModel.setMultivalued(false);
		csticModel.setReadonly(false);
		csticModel.setValueType(valueType);
		return csticModel;
	}

	protected CsticModel createSelection(final int valueType, final int numOptions, final boolean isMultivalued)
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setConstrained(true);
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(createAssignableValueList(numOptions));
		csticModel.setEntryFieldMask(null);
		csticModel.setMultivalued(isMultivalued);
		csticModel.setReadonly(false);
		csticModel.setValueType(valueType);
		csticModel.setStaticDomainLength(csticModel.getAssignableValues().size());
		return csticModel;
	}

	private List<CsticValueModel> createAssignableValueList(final int size)
	{
		final List<CsticValueModel> values = new ArrayList<>(size);
		for (int ii = 0; ii < size; ii++)
		{
			final CsticValueModel value = new CsticValueModelImpl();
			value.setName(String.valueOf(ii));
			values.add(value);
		}
		return values;
	}

	private List<CsticValueModel> createAssignedValueList(final int size)
	{
		final List<CsticValueModel> values = new ArrayList<>(size);
		for (int ii = 0; ii < size; ii++)
		{
			final CsticValueModel value = new CsticValueModelImpl();
			values.add(value);
		}
		return values;
	}


	private List<CsticValueData> createValueDTOList(final int size)
	{
		final List<CsticValueData> values = new ArrayList<>(size);
		for (int ii = 0; ii < size; ii++)
		{
			final CsticValueData value = new CsticValueData();
			values.add(value);
		}
		return values;
	}


	@Test
	public void givenFloatThenUiTypeNumeric() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_FLOAT);
		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
		assertEquals("Wrong UI  type", UiValidationType.NUMERIC, actualValidationType);
	}

	@Test
	public void givenFloatReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_FLOAT);
		csticModel.setReadonly(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}

	@Test
	public void givenIntegerThenUiTypeNumeric() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_INTEGER);
		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
	}

	@Test
	public void givenIntegerReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_INTEGER);
		csticModel.setReadonly(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}


	@Test
	public void givenStringThenUiTypeString() throws Exception
	{
		csticModel = createSimpleInput();

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.STRING, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);

	}

	@Test
	public void givenStringReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setReadonly(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenStringAndMultiValueThenUiTypeCheckbox() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 1, true);
		csticModel.setConstrained(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX, actual);
	}

	@Test
	public void givenStringAndMultiValueReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 1, true);
		csticModel.setReadonly(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenStringAnd4ValuesThenUiTypeRadio() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 4, false);
		csticModel.setConstrained(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON, actual);
	}

	@Test
	public void givenFloatAnd4ValuesThenUiTypeRadio() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 4, false);
		csticModel.setConstrained(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON, actual);
	}

	@Test
	public void givenFloatAnd4ValuesReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 4, false);
		csticModel.setReadonly(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenStringAnd5ValuesThenUiTypeDDLB() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 5, false);
		csticModel.setConstrained(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN, actual);
	}

	@Test
	public void givenStringAnd5ValuesReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 5, false);
		csticModel.setReadonly(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenIntAnd5ValuesThenUiTypeDDLB() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_INTEGER, 5, false);
		csticModel.setConstrained(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN, actual);
	}


	@Test
	public void givenStringAndMultivaluedDomainThenUiCheckboxList() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 2, true);
		csticModel.setConstrained(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX_LIST, actual);
	}

	@Test
	public void givenStringAndMultivaluedDomainAndAllowsAdditionalValueThenNotImplemented() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 2, true);
		csticModel.setConstrained(true);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}


	@Test
	public void givenFloatAndMultivaluedDomainThenUiCheckboxList() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 2, true);
		csticModel.setConstrained(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX_LIST, actual);
	}

	@Test
	public void givenUndefinedThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_UNDEFINED);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenDateThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_DATE);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenCurrencyThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_CURRENCY);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenClassThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_CLASS);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenBooleanThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_BOOLEAN);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}

	@Test
	public void givenSingleValueAllowsAdditionalValueStringThenDropDownAdditionalInputString() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 6, false);
		csticModel.setAllowsAdditionalValues(true);


		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}

	@Test
	public void givenSingleValueAllowsAdditionalValueNumericThenDropDownAdditionalInputNumeric() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 6, false);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NUMERIC, actualValidationType);
	}

	@Test
	public void givenSingleValueAllowsAdditionalValueStringThenRadioAdditionalInputString() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 3, false);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}


	@Test
	public void givenSingleValueAllowsAdditionalValueStringNoDomainThenInputString() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 0, false);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.STRING, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}



	@Test
	public void givenSingleValueAllowsAdditionalValueNumericSingleDomainSelectedThenInputNumeric() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 1, false);
		csticModel.setAllowsAdditionalValues(true);
		csticModel.setSingleValue(csticModel.getAssignableValues().get(0).getName());

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
		assertEquals("Wrong UI  type", UiValidationType.NUMERIC, actualValidationType);
	}


	@Test
	public void givenSingleValueAllowsAdditionalValueNumericSingleDomainNotSelectedThenRadioAdditionalInputNumeric()
			throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 1, false);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = classUnderTest.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NUMERIC, actualValidationType);
	}

	@Test
	public void givenSingleSelectionIntervalThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_INTEGER, 0, false);
		csticModel.setConstrained(false);
		csticModel.setIntervalInDomain(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
	}

	@Test
	public void givenMultiValuedIntervalThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_INTEGER, 4, true);
		csticModel.setIntervalInDomain(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenScientificThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_FLOAT);
		csticModel.setEntryFieldMask("_,____.__EE");

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenMultivaluedStringWithoutStaticDomainThenUiTypeCheckboxList() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setMultivalued(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX_LIST, actual);
	}

	@Test
	public void givenMultivaluedStringWithoutStaticDomainButAssignedValuesThenUiTypeCheckboxList() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setAssignedValuesWithoutCheckForChange(createAssignedValueList(2));
		csticModel.setMultivalued(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX_LIST, actual);
	}


	@Test
	public void givenStringWithTemplateThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setEntryFieldMask("abcd-efg");

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenIntegerWithIntervalWithoutAddValThenUiTypeNumeric() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_INTEGER);
		csticModel.setIntervalInDomain(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
	}

	@Test
	public void givenIntegerWithIntervalWithAddValThenUiTypeNumeric() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_INTEGER);
		csticModel.setIntervalInDomain(true);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
	}

	@Test
	public void testHasValueImages() throws Exception
	{

		final CsticValueData value = new CsticValueData();

		// Test media null
		assertFalse(classUnderTest.hasValueImage(value));

		// test empty media-list
		final List<ImageData> images = new ArrayList<>();
		value.setMedia(images);

		assertFalse(classUnderTest.hasValueImage(value));

		// Second image is value image
		final ImageData image = new ImageData();
		image.setFormat(CPQImageType.VALUE_IMAGE.toString());
		images.add(image);
		final ImageData image2 = new ImageData();
		image2.setFormat(CPQImageType.VALUE_IMAGE.toString());
		images.add(image2);

		assertTrue(classUnderTest.hasValueImage(value));
	}

	@Test
	public void testHasCsticValueImages() throws Exception
	{
		final CsticData data = new CsticData();
		final List<CsticValueData> domainValues = new ArrayList();
		data.setDomainvalues(domainValues);

		//	Test:  no domain values
		assertFalse(classUnderTest.hasCsticValueImages(data));

		final CsticValueData value = new CsticValueData();
		domainValues.add(value);
		final CsticValueData value2 = new CsticValueData();
		domainValues.add(value2);
		final CsticValueData value3 = new CsticValueData();
		domainValues.add(value3);

		final List<ImageData> images = new ArrayList<>();
		value.setMedia(images);
		value2.setMedia(images);
		value3.setMedia(images);

		// test: empty media-lists
		assertFalse(classUnderTest.hasCsticValueImages(data));

		final List<ImageData> images1 = new ArrayList<>();
		ImageData image = new ImageData();
		images1.add(image);
		ImageData image2 = new ImageData();
		image2.setFormat(CPQImageType.CSTIC_IMAGE.toString());
		images1.add(image2);
		images1.add(image);
		value2.setMedia(images1);

		// test none of values has image
		assertFalse(classUnderTest.hasCsticValueImages(data));

		final List<ImageData> images2 = new ArrayList<>();
		image = new ImageData();
		images2.add(image);
		image2 = new ImageData();
		image2.setFormat(CPQImageType.VALUE_IMAGE.toString());
		images2.add(image2);
		images2.add(image);
		value2.setMedia(images2);

		// test second value has image
		assertTrue(classUnderTest.hasCsticValueImages(data));
	}

	@Test
	public void givenSingleSelectionImage() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 5, false);
		final CsticData data = createCsticDataWithOneValueImage(5, 2);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel, data);
		assertEquals("Wrong UI type", UiType.SINGLE_SELECTION_IMAGE, actual);
	}

	@Test
	public void givenSingleSelectionImageReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 7, false);
		csticModel.setReadonly(true);
		final CsticData data = createCsticDataWithOneValueImage(7, 1);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel, data);
		assertEquals("Wrong UI type", UiType.READ_ONLY_SINGLE_SELECTION_IMAGE, actual);
	}

	@Test
	public void givenMultiSelectionImage() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 7, true);
		final CsticData data = createCsticDataWithOneValueImage(7, 0);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel, data);
		assertEquals("Wrong UI type", UiType.MULTI_SELECTION_IMAGE, actual);
	}

	@Test
	public void givenMultiSelectionImageReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 5, true);
		csticModel.setReadonly(true);
		final CsticData data = createCsticDataWithOneValueImage(5, 4);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel, data);
		assertEquals("Wrong UI type", UiType.READ_ONLY_MULTI_SELECTION_IMAGE, actual);
	}

	@Test
	public void givenMultiSelectionImageOriginallySingleCheckbox() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 1, true);
		csticModel.setConstrained(true);
		final CsticData data = createCsticDataWithOneValueImage(1, 0);

		final UiType actual = classUnderTest.findUiTypeForCstic(csticModel, data);
		assertEquals("Wrong UI type", UiType.MULTI_SELECTION_IMAGE, actual);
	}

	protected CsticData createCsticDataWithOneValueImage(final int valueNumber, final int imageValuePosition)
	{
		final CsticData data = new CsticData();
		final List<CsticValueData> domainValues = createValueDTOList(valueNumber);
		data.setDomainvalues(domainValues);

		final List<ImageData> images = new ArrayList<>();
		final ImageData image = new ImageData();
		image.setFormat(CPQImageType.VALUE_IMAGE.toString());
		images.add(image);
		domainValues.get(imageValuePosition).setMedia(images);
		return data;
	}

	@Test
	public void testMergeUiTypeListLowMem_bothEmpty()
	{
		final List<UiType> list1 = Collections.emptyList();
		final List<UiType> list2 = Collections.emptyList();
		final List<UiType> mergedLists = classUnderTest.mergeUiTypeListLowMem(list1, list2);
		assertSame(Collections.emptyList(), mergedLists);
	}

	@Test
	public void testMergeUiTypeListLowMem_list2Empty()
	{
		final List<UiType> list2 = Collections.emptyList();
		final List<UiType> list1 = new ArrayList<>();
		list1.add(UiType.STRING);
		final List<UiType> mergedLists = classUnderTest.mergeUiTypeListLowMem(list1, list2);
		assertSame(list1, mergedLists);
	}

	@Test
	public void testMergeUiTypeListLowMem_list1Empty()
	{
		final List<UiType> list1 = Collections.emptyList();
		final List<UiType> list2 = new ArrayList<>();
		list2.add(UiType.STRING);
		final List<UiType> mergedLists = classUnderTest.mergeUiTypeListLowMem(list1, list2);
		assertSame(list2, mergedLists);
	}

	@Test
	public void testMergeUiTypeListLowMem_bothFilled()
	{
		final List<UiType> list2 = new ArrayList<>();
		list2.add(UiType.STRING);
		final List<UiType> list1 = new ArrayList<>();
		list1.add(UiType.CHECK_BOX);
		final List<UiType> mergedLists = classUnderTest.mergeUiTypeListLowMem(list1, list2);
		assertEquals(2, mergedLists.size());
	}

	@Test
	public void testAddUiTypeToListLowMem_empty()
	{
		final ArrayList<UiType> oldList = new ArrayList<UiType>();
		final List<UiType> newList = classUnderTest.addUiTypeToListLowMem(oldList, UiType.STRING);
		assertEquals(Collections.singletonList(UiType.STRING).getClass(), newList.getClass());
	}

	@Test
	public void testAddUiTypeToListLowMem_notEmpty()
	{
		final ArrayList<UiType> oldList = new ArrayList<UiType>();
		oldList.add(UiType.CHECK_BOX);
		final List<UiType> newList = classUnderTest.addUiTypeToListLowMem(oldList, UiType.STRING);
		assertEquals(2, newList.size());
	}

	@Test
	public void testChooseUiType_notImplemented()
	{
		final UiType choosenType = classUnderTest.chooseUiType(Collections.emptyList(), new CsticModelImpl());
		assertEquals(UiType.NOT_IMPLEMENTED, choosenType);
	}

	@Test
	public void testChooseUiType()
	{
		final UiType choosenType = classUnderTest.chooseUiType(Collections.singletonList(UiType.CHECK_BOX), new CsticModelImpl());
		assertEquals(UiType.CHECK_BOX, choosenType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testChooseUiType_ambigious()
	{
		final List<UiType> list = new ArrayList<UiType>();
		list.add(UiType.STRING);
		list.add(UiType.NUMERIC);
		classUnderTest.chooseUiType(list, new CsticModelImpl());
	}

	@Test
	public void testChooseUiValidationType_none()
	{
		final UiValidationType choosenType = classUnderTest.chooseUiValidationType(Collections.emptyList(), new CsticModelImpl());
		assertEquals(UiValidationType.NONE, choosenType);
	}

	@Test
	public void testChooseUiValidationType()
	{
		final UiValidationType choosenType = classUnderTest
				.chooseUiValidationType(Collections.singletonList(UiValidationType.NUMERIC), new CsticModelImpl());
		assertEquals(UiValidationType.NUMERIC, choosenType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testChooseUiValidationType_ambigious()
	{
		final List<UiValidationType> list = new ArrayList<UiValidationType>();
		list.add(UiValidationType.NONE);
		list.add(UiValidationType.NUMERIC);
		classUnderTest.chooseUiValidationType(list, new CsticModelImpl());
	}

	@Test
	public void testIsReadonly()
	{
		csticModel.setConstrained(true);
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(null);

		assertTrue(classUnderTest.isReadonly(csticModel, false));

		csticModel.setConstrained(false);
		csticModel.setAllowsAdditionalValues(true);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.isReadonly(csticModel, false));

		csticModel.setConstrained(true);
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(Collections.EMPTY_LIST);

		assertTrue(classUnderTest.isReadonly(csticModel, false));

		csticModel.setConstrained(false);
		csticModel.setAllowsAdditionalValues(true);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.isReadonly(csticModel, false));

		csticModel.setConstrained(false);
		csticModel.setAllowsAdditionalValues(true);
		csticModel.setAssignableValues(Collections.EMPTY_LIST);

		assertFalse(classUnderTest.isReadonly(csticModel, false));

		csticModel.setConstrained(true);
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(createAssignableValueList(4));

		assertFalse(classUnderTest.isReadonly(csticModel, false));

		csticModel.setConstrained(false);
		csticModel.setAllowsAdditionalValues(true);
		csticModel.setAssignableValues(createAssignableValueList(4));

		assertFalse(classUnderTest.isReadonly(csticModel, false));
	}

	@Test
	public void testIsConstrainedOrHasAssignableValues()
	{
		csticModel.setConstrained(true);
		csticModel.setAssignableValues(createAssignableValueList(4));

		assertTrue(classUnderTest.isConstrainedOrHasAssignableValues(csticModel, false));

		csticModel.setConstrained(true);
		csticModel.setAssignableValues(Collections.EMPTY_LIST);

		assertTrue(classUnderTest.isConstrainedOrHasAssignableValues(csticModel, false));

		csticModel.setConstrained(false);
		csticModel.setAssignableValues(createAssignableValueList(4));

		assertTrue(classUnderTest.isConstrainedOrHasAssignableValues(csticModel, false));

		csticModel.setConstrained(false);
		csticModel.setAssignableValues(Collections.EMPTY_LIST);

		assertFalse(classUnderTest.isConstrainedOrHasAssignableValues(csticModel, false));
	}

	@Test
	public void testIsIntervallBasedInput()
	{
		csticModel.setIntervalInDomain(false);
		csticModel.setAllowsAdditionalValues(false);

		assertFalse(classUnderTest.isIntervallBasedInput(csticModel));

		csticModel.setIntervalInDomain(false);
		csticModel.setAllowsAdditionalValues(true);

		assertFalse(classUnderTest.isIntervallBasedInput(csticModel));

		csticModel.setIntervalInDomain(true);
		csticModel.setAllowsAdditionalValues(true);

		assertFalse(classUnderTest.isIntervallBasedInput(csticModel));

		csticModel.setIntervalInDomain(true);
		csticModel.setAllowsAdditionalValues(false);

		assertTrue(classUnderTest.isIntervallBasedInput(csticModel));
	}

	@Test
	public void testIsSimpleString()
	{
		csticModel.setValueType(CsticModel.TYPE_STRING);
		csticModel.setEntryFieldMask(null);

		assertTrue(classUnderTest.isSimpleString(csticModel, false));

		csticModel.setValueType(CsticModel.TYPE_STRING);
		csticModel.setEntryFieldMask("");

		assertTrue(classUnderTest.isSimpleString(csticModel, false));

		csticModel.setValueType(CsticModel.TYPE_STRING);
		csticModel.setEntryFieldMask("entryFieldMask");

		assertFalse(classUnderTest.isSimpleString(csticModel, false));
	}

	@Test
	public void testIsSimpleNumber()
	{
		csticModel.setValueType(CsticModel.TYPE_INTEGER);
		csticModel.setEntryFieldMask(null);

		assertTrue(classUnderTest.isSimpleNumber(csticModel, false));

		csticModel.setValueType(CsticModel.TYPE_FLOAT);
		csticModel.setEntryFieldMask("T");

		assertTrue(classUnderTest.isSimpleNumber(csticModel, false));

		csticModel.setValueType(CsticModel.TYPE_FLOAT);
		csticModel.setEntryFieldMask("E");

		assertFalse(classUnderTest.isSimpleNumber(csticModel, false));
	}

	@Test
	public void testeditableWithAdditionalValue()
	{
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setReadonly(false);
		csticModel.setIntervalInDomain(false);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.editableWithAdditionalValue(csticModel, false));

		csticModel.setAllowsAdditionalValues(true);
		csticModel.setReadonly(false);
		csticModel.setIntervalInDomain(false);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.editableWithAdditionalValue(csticModel, false));

		csticModel.setAllowsAdditionalValues(true);
		csticModel.setReadonly(false);
		csticModel.setIntervalInDomain(true);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.editableWithAdditionalValue(csticModel, false));

		csticModel.setAllowsAdditionalValues(false);
		csticModel.setReadonly(true);
		csticModel.setIntervalInDomain(false);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.editableWithAdditionalValue(csticModel, false));

		csticModel.setAllowsAdditionalValues(false);
		csticModel.setReadonly(true);
		csticModel.setIntervalInDomain(true);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.editableWithAdditionalValue(csticModel, false));

		csticModel.setAllowsAdditionalValues(true);
		csticModel.setReadonly(true);
		csticModel.setIntervalInDomain(true);
		csticModel.setAssignableValues(null);

		assertFalse(classUnderTest.editableWithAdditionalValue(csticModel, false));
	}

	@Test
	public void testIsAdditionalValueWithoutDomian()
	{
		csticModel.setAssignableValues(Collections.EMPTY_LIST);
		csticModel.setSingleValue(null);
		csticModel.setAllowsAdditionalValues(false);

		assertFalse(classUnderTest.isAdditionalValueWithoutDomian(csticModel));

		final List<CsticValueModel> assignableValues = createAssignableValueList(1);
		csticModel.setAssignableValues(assignableValues);
		csticModel.setSingleValue("0");
		csticModel.setAllowsAdditionalValues(false);

		assertFalse(classUnderTest.isAdditionalValueWithoutDomian(csticModel));

		csticModel.setSingleValue("valueName");
		csticModel.setAllowsAdditionalValues(false);

		assertFalse(classUnderTest.isAdditionalValueWithoutDomian(csticModel));
	}

	@Test
	public void testIsInput()
	{
		//not input field
		csticModel.setReadonly(true);
		csticModel.setMultivalued(true);
		assertFalse(classUnderTest.isInput(csticModel, false));

		csticModel.setReadonly(false);
		csticModel.setMultivalued(false);
		csticModel.setValueType(CsticModel.TYPE_STRING);
		csticModel.setEntryFieldMask(null);

		//simple input
		csticModel.setAssignableValues(Collections.EMPTY_LIST);
		csticModel.setConstrained(false);

		assertTrue(classUnderTest.isInput(csticModel, false));

		//interval based input
		csticModel.setIntervalInDomain(true);
		csticModel.setAllowsAdditionalValues(false);

		assertTrue(classUnderTest.isInput(csticModel, false));

		//additional value without domian
		csticModel.setIntervalInDomain(false);
		final List<CsticValueModel> assignableValues = createAssignableValueList(1);
		csticModel.setAssignableValues(assignableValues);
		csticModel.setSingleValue("0");
		csticModel.setAllowsAdditionalValues(true);
		assertTrue(classUnderTest.isInput(csticModel, false));
	}

	@Test
	public void testIsMultiSelectionImage()
	{
		csticModel.setValueType(CsticModel.TYPE_STRING);
		csticModel.setIntervalInDomain(true);
		assertFalse(classUnderTest.isMultiSelectionImage(csticModel, false, false));
		assertFalse(classUnderTest.isMultiSelectionImage(csticModel, false, true));

		csticModel.setIntervalInDomain(false);
		csticModel.setMultivalued(true);
		assertFalse(classUnderTest.isMultiSelectionImage(csticModel, false, false));
		assertTrue(classUnderTest.isMultiSelectionImage(csticModel, false, true));

		csticModel.setAllowsAdditionalValues(true);
		assertFalse(classUnderTest.isMultiSelectionImage(csticModel, false, true));
		assertFalse(classUnderTest.isMultiSelectionImage(csticModel, false, false));
	}

	@Test
	public void testIsRadioButtonReturnsTrue()
	{
		final CsticModel model = new CsticModelImpl();
		model.setMultivalued(false);
		model.setValueType(CsticModel.TYPE_STRING);
		final List<CsticValueModel> assignableValues = new ArrayList<>();
		for (int i = 1; i <= 3; i++)
		{
			final CsticValueModel assignableValue = new CsticValueModelImpl();
			assignableValue.setName("cstic_" + i);
			assignableValues.add(assignableValue);
		}
		model.setAssignableValues(assignableValues);
		model.setAllowsAdditionalValues(false);
		model.setReadonly(false);
		assertTrue(classUnderTest.isRadioButton(model, false, false));
	}

	@Test
	public void testIsRadioButtonReturnsFalse()
	{
		final CsticModel model = new CsticModelImpl();
		model.setMultivalued(false);
		model.setValueType(CsticModel.TYPE_STRING);
		final List<CsticValueModel> assignableValues = new ArrayList<>();
		for (int i = 1; i <= 3; i++)
		{
			final CsticValueModel assignableValue = new CsticValueModelImpl();
			assignableValue.setName("cstic_" + i);
			assignableValues.add(assignableValue);
		}
		model.setAssignableValues(assignableValues);
		model.setAllowsAdditionalValues(true);
		model.setReadonly(true);
		assertFalse(classUnderTest.isRadioButton(model, false, true));
	}
}








