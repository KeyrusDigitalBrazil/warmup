/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.excel.translators.classification;

import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.BOOLEAN;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.DATE;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.NUMBER;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.Impex;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.ExcelImportContext;
import com.hybris.backoffice.excel.util.ExcelDateUtils;


@RunWith(MockitoJUnitRunner.class)
public class ExcelClassificationJavaTypeTranslatorTest
{
	private static final ExcelImportContext EMPTY_CTX = new ExcelImportContext();

	@Mock
	private ClassAttributeAssignmentModel classAttributeAssignment;
	@Mock
	private ExcelClassificationAttribute excelAttribute;
	@Mock
	private ProductModel productToExport;
	@Mock
	private LocalizedFeature feature;

	@Mock
	private ClassificationAttributeHeaderValueCreator classificationAttributeHeaderValueCreator;
	@Mock
	private ExcelDateUtils excelDateUtils;
	@Mock
	private ClassificationService classificationService;
	@Mock
	private ClassificationSystemService classificationSystemService;
	@Mock
	private CommonI18NService commonI18NService;

	@InjectMocks
	private ExcelClassificationJavaTypeTranslator translator;


	@Before
	public void setUp()
	{
		doReturn(classAttributeAssignment).when(excelAttribute).getAttributeAssignment();
		doReturn(classAttributeAssignment).when(feature).getClassAttributeAssignment();
		doReturn(feature).when(classificationService).getFeature(productToExport, classAttributeAssignment);
		when(excelDateUtils.exportDate(any())).thenAnswer(invocationOnMock -> {
			final Date date = (Date) invocationOnMock.getArguments()[0];
			return DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC")).format(date.toInstant());
		});
		when(excelDateUtils.importDate(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
	}

	@Test
	public void testCanHandle()
	{
		final Set<ClassificationAttributeTypeEnum> supportedTypes = EnumSet.of(NUMBER, STRING, BOOLEAN, DATE);

		final Set<ClassificationAttributeTypeEnum> unsupportedTypes = Sets.newHashSet(ClassificationAttributeTypeEnum.values());
		unsupportedTypes.removeAll(supportedTypes);

		// check supported types
		supportedTypes.forEach(type -> {
			// given
			doReturn(type).when(classAttributeAssignment).getAttributeType();

			// when
			final boolean result = translator.canHandle(excelAttribute);

			// then
			assertThat(result).as("Should be able to handle type: " + type).isTrue();
		});

		// check unsupported types
		unsupportedTypes.forEach(type -> {
			// given
			doReturn(type).when(classAttributeAssignment).getAttributeType();

			// when
			final boolean result = translator.canHandle(excelAttribute);

			// then
			assertThat(result).as("Should not handle type: " + type).isFalse();
		});
	}

	@Test
	public void shouldImportFeatureValues()
	{
		final Object[][] testData = {
				// excel cell value, attribute header, attribute type, expected output
				{123L, "Identifier", NUMBER, 123L},
				{"A description of a product.", "Description" , STRING, "A description of a product."},
				{true,"Approved" , BOOLEAN, true},
				{"2018-03-04", "Identifier", DATE, "2018-03-04"},
				{"123:kg", "Identifier", NUMBER, "123:kg"},
				{"3:g,8kg,12t", "Identifier", NUMBER, "3:g,8kg,12t"},
				{"one,two,three", "Identifier", STRING, "one,two,three"},
				{"2018-03-04,2018-03-05", "Identifier", DATE, "2018-03-04,2018-03-05"},
		};

		for (final Object[] testDataRow: testData)
		{
			// given
			final Serializable excelCellValue = (Serializable) testDataRow[0];
			final String attributeHeader = (String) testDataRow[1];
			final ClassificationAttributeTypeEnum attributeType = (ClassificationAttributeTypeEnum) testDataRow[2];
			final Serializable expectedOutput = (Serializable) testDataRow[3];

			given(classificationAttributeHeaderValueCreator.create(eq(excelAttribute), any())).willReturn(attributeHeader);
			final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, excelCellValue, null,
					Collections.emptyList());

			// when
			final Impex impex = translator.importData(excelAttribute, importParameters, EMPTY_CTX);

			// then
			assertThat(impex.findUpdates(ProductModel._TYPECODE).getImpexTable().cellSet()) //
					.hasSize(1) //
					.hasOnlyOneElementSatisfying(element -> {
						assertThat(element.getValue()).isEqualTo(expectedOutput);
						assertThat(element.getColumnKey()).isNotNull().extracting("name").containsOnly(attributeHeader);
					});
		}
	}

	@Test
	public void shouldExportFeatureValues()
	{
		for (final TestData testData: createListOfTestData())
		{
			// given
			initializeTest(testData);

			// when
			final Optional<String> result = translator.exportData(excelAttribute, productToExport);

			// then
			assertThat(result).as("Should export for test data: " + testData).isPresent();
			assertThat(result.get()).as("Should export for test data: " + testData).isEqualTo(testData.getExpectedOutput());
		}
	}

	private List<TestData> createListOfTestData()
	{
		final List<TestData> listOfTestData = new ArrayList<>();

		// single values

		final TestData singleInteger = new TestData();
		singleInteger.addFeatureValue(1);
		singleInteger.setExpectedOutput("1");
		listOfTestData.add(singleInteger);

		final TestData singleDouble = new TestData();
		singleDouble.addFeatureValue(1.1);
		singleDouble.setExpectedOutput("1.1");
		listOfTestData.add(singleDouble);

		final TestData singleBoolean = new TestData();
		singleBoolean.addFeatureValue(true);
		singleBoolean.setExpectedOutput("true");
		listOfTestData.add(singleBoolean);

		final TestData singleString = new TestData();
		singleString.addFeatureValue("Test string.");
		singleString.setExpectedOutput("Test string.");
		listOfTestData.add(singleString);

		final TestData singleDate = new TestData();
		singleDate.addFeatureValue(createDate(2018, 4, 23));
		singleDate.setExpectedOutput("2018-04-23");
		listOfTestData.add(singleDate);

		// single values with units

		final TestData singleDoubleWithUnit = new TestData();
		singleDoubleWithUnit.addFeatureValueWithUnit(1, "kg");
		singleDoubleWithUnit.setExpectedOutput("1:kg");
		listOfTestData.add(singleDoubleWithUnit);

		// multiValued

		final TestData multiInteger = new TestData();
		multiInteger.addFeatureValue(1);
		multiInteger.addFeatureValue(5);
		multiInteger.addFeatureValue(7);
		multiInteger.setExpectedOutput("1,5,7");
		listOfTestData.add(multiInteger);

		final TestData multiDouble = new TestData();
		multiDouble.addFeatureValue(1.1);
		multiDouble.addFeatureValue(5.3);
		multiDouble.addFeatureValue(67.23);
		multiDouble.setExpectedOutput("1.1,5.3,67.23");
		listOfTestData.add(multiDouble);

		final TestData multiDate = new TestData();
		multiDate.addFeatureValue(createDate(2017, 3, 9));
		multiDate.addFeatureValue(createDate(2018, 4, 23));
		multiDate.setExpectedOutput("2017-03-09,2018-04-23");
		listOfTestData.add(multiDate);

		// multiValued with units

		final TestData multiIntegerWithUnit = new TestData();
		multiIntegerWithUnit.addFeatureValueWithUnit(1, "g");
		multiIntegerWithUnit.addFeatureValueWithUnit(5, "kg");
		multiIntegerWithUnit.addFeatureValueWithUnit(7, "t");
		multiIntegerWithUnit.setExpectedOutput("1:g,5:kg,7:t");
		listOfTestData.add(multiIntegerWithUnit);

		// localized multiValued with units

		final TestData localizedMultiIntegerWithUnit = new TestData();
		localizedMultiIntegerWithUnit.addFeatureValueWithUnit(1, "g");
		localizedMultiIntegerWithUnit.addFeatureValueWithUnit(5, "kg");
		localizedMultiIntegerWithUnit.addFeatureValueWithUnit(7, "t");
		localizedMultiIntegerWithUnit.setLang("en");
		localizedMultiIntegerWithUnit.setExpectedOutput("1:g,5:kg,7:t");
		listOfTestData.add(localizedMultiIntegerWithUnit);

		final TestData localizedMultiDoubleWithUnit = new TestData();
		localizedMultiDoubleWithUnit.addFeatureValueWithUnit(1.7, "g");
		localizedMultiDoubleWithUnit.addFeatureValueWithUnit(55.2, "kg");
		localizedMultiDoubleWithUnit.addFeatureValueWithUnit(70.123, "t");
		localizedMultiDoubleWithUnit.setLang("de");
		localizedMultiDoubleWithUnit.setExpectedOutput("1.7:g,55.2:kg,70.123:t");
		listOfTestData.add(localizedMultiDoubleWithUnit);

		// localized multiValued

		final TestData localizedMultiString = new TestData();
		localizedMultiString.addFeatureValue("first");
		localizedMultiString.addFeatureValue("second");
		localizedMultiString.addFeatureValue("third");
		localizedMultiString.setLang("fr");
		localizedMultiString.setExpectedOutput("first,second,third");
		listOfTestData.add(localizedMultiString);

		return listOfTestData;
	}

	private Date createDate(final int year, final int month, final int dayOfMonth)
	{
		return Date.from(LocalDateTime.of(year, month, dayOfMonth, 0, 0, 0 ).toInstant(ZoneOffset.UTC));
	}

	private void initializeTest(final TestData testData)
	{
		doReturn(testData.isMultiValued()).when(classAttributeAssignment).getMultiValued();
		doReturn(testData.isLocalized()).when(classAttributeAssignment).getLocalized();
		doReturn(testData.getLang()).when(excelAttribute).getIsoCode();
		doReturn(testData.getLocale()).when(commonI18NService).getLocaleForIsoCode(testData.getLang());

		if (testData.isLocalized())
		{
			if (testData.isMultiValued())
			{
				doReturn(testData.getFeatureValues()).when(feature).getValues(testData.getLocale());
			}
			else
			{
				doReturn(testData.getFeatureValues().get(0)).when(feature).getValue(testData.getLocale());
			}
		}
		else
		{
			if (testData.isMultiValued())
			{
				doReturn(testData.getFeatureValues()).when(feature).getValues();
			}
			else
			{
				doReturn(testData.getFeatureValues().get(0)).when(feature).getValue();
			}
		}
	}
	static class TestData {
		private final List<FeatureValue> featureValues = new ArrayList<>();
		private String lang;

		private String expectedOutput;

		boolean isMultiValued()
		{
			return featureValues.size() > 1;
		}

		boolean isLocalized()
		{
			return lang != null;
		}

		Locale getLocale()
		{
			return isLocalized()? Locale.forLanguageTag(lang) : null;
		}

		void addFeatureValue(final Object value)
		{
			featureValues.add(createFeatureValue(value, null));
		}

		void addFeatureValueWithUnit(final Object value, final String unit)
		{
			featureValues.add(createFeatureValue(value, unit));
		}

		List<FeatureValue> getFeatureValues()
		{
			return featureValues;
		}

		String getLang()
		{
			return lang;
		}

		void setLang(final String lang)
		{
			this.lang = lang;
		}

		String getExpectedOutput()
		{
			return expectedOutput;
		}

		void setExpectedOutput(final String expectedOutput)
		{
			this.expectedOutput = expectedOutput;
		}

		private FeatureValue createFeatureValue(final Object value, final String unit)
		{
			final FeatureValue featureValue = mock(FeatureValue.class);
			final ClassificationAttributeUnitModel unitModel = mock(ClassificationAttributeUnitModel.class);

			when(featureValue.getValue()).thenReturn(value);
			when(unitModel.getCode()).thenReturn(unit);
			if (unit != null)
			{
				doReturn(unitModel).when(featureValue).getUnit();
			}
			else
			{
				doReturn(null).when(featureValue).getUnit();
			}
			return featureValue;
		}

		@Override
		public String toString()
		{
			final String featureValuesAsString = featureValues.stream()//
					.map(f -> f.getUnit() != null ? f.getValue() + ":" + f.getUnit().getCode() : f.getValue().toString())//
					.collect(Collectors.joining(","));
			return String.format("TestData{featureValues=%s, lang=%s, expectedOutput=%s}", featureValuesAsString, lang,
					expectedOutput);
		}
	}

	@Test
	public void shouldReturnReferencePatternForDateFromExcelDateUtils()
	{
		// given
		given(classAttributeAssignment.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.DATE);
		given(excelDateUtils.getDateTimeFormat()).willReturn("dd.MM.yyyy HH:mm:s");

		// when
		translator.referenceFormat(excelAttribute);

		// then
		verify(excelDateUtils).getDateTimeFormat();
	}
}
