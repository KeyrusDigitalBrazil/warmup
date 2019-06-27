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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.Impex;
import com.hybris.backoffice.excel.data.ImpexHeaderValue;
import com.hybris.backoffice.excel.data.ImpexValue;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.ExcelImportContext;
import com.hybris.backoffice.excel.importing.parser.splitter.UnitExcelParserSplitter;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;


@RunWith(MockitoJUnitRunner.class)
public class AbstractClassificationAttributeTranslatorTest
{

	@Mock
	private ExcelClassificationAttribute excelClassificationAttribute;

	@Mock
	private ClassificationSystemService classificationSystemService;

	@Spy
	private AbstractClassificationAttributeTranslator abstractClassificationAttributeTranslator;

	@Before
	public void setUp()
	{
		given(abstractClassificationAttributeTranslator.getClassificationSystemService()).willReturn(classificationSystemService);
	}

	@Test
	public void shouldImportDataIfImportedValueIsNull()
	{
		// given
		final ImportParameters anyImportParameters = mock(ImportParameters.class);
		final ExcelImportContext anyExcelImportContext = mock(ExcelImportContext.class);

		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(assignment);
		given(abstractClassificationAttributeTranslator.importValue(excelClassificationAttribute, anyImportParameters,
				new ExcelImportContext())).willReturn(null);

		// when
		final Impex result = abstractClassificationAttributeTranslator.importData(excelClassificationAttribute, anyImportParameters,
				anyExcelImportContext);

		// then no data is imported
		assertThat(result.getImpexes()).isEmpty();
	}

	@Test
	public void shouldImportExcelClassificationAttributeOnly()
	{
		// given
		final ExcelAttribute notAClassificationAttribute = mock(ExcelAttribute.class);
		final ImportParameters anyImportParameters = mock(ImportParameters.class);
		final ExcelImportContext anyExcelImportContext = mock(ExcelImportContext.class);

		// when
		final Impex result = abstractClassificationAttributeTranslator.importData(notAClassificationAttribute, anyImportParameters,
				anyExcelImportContext);

		// then no data is imported
		assertThat(result.getImpexes()).isEmpty();
		verify(abstractClassificationAttributeTranslator, never()).importValue(any(), any(), any());
	}

	@Test
	public void shouldImportValueAndPutItToImpex()
	{
		// given
		final String typeCode = "typeCode";
		final ImportParameters importParameters = mock(ImportParameters.class);
		given(importParameters.getTypeCode()).willReturn(typeCode);
		final ExcelImportContext excelImportContext = mock(ExcelImportContext.class);
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(assignment);
		doReturn(new ImpexValue("value", new ImpexHeaderValue.Builder("header").build()))
				.when(abstractClassificationAttributeTranslator)
				.importValue(excelClassificationAttribute, importParameters, excelImportContext);

		// when
		final Impex result = abstractClassificationAttributeTranslator.importData(excelClassificationAttribute, importParameters,
				excelImportContext);

		// then
		verify(abstractClassificationAttributeTranslator).importValue(excelClassificationAttribute, importParameters,
				excelImportContext);
		assertThat(result.findUpdates(typeCode).getImpexTable().cellSet()) //
				.hasSize(1) //
				.hasOnlyOneElementSatisfying(cell -> {
					assertThat(cell.getValue()).isEqualTo("value");
					assertThat(cell.getColumnKey()).isNotNull().extracting("name").containsOnly("header");
					assertThat(cell.getRowKey()).isZero();
				});
	}

	@Test
	public void shouldCalculateReferenceFormatForAttributeWithUnit()
	{
		// given
		final String unitType = "unitType";
		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeUnitModel unit = mock(ClassificationAttributeUnitModel.class);
		final List<ClassificationAttributeUnitModel> unitsForUnitType = Arrays.asList(createUnit("kg", 0.001), createUnit("g", 1));
		final ClassificationSystemVersionModel classificationSystemVersion = mock(ClassificationSystemVersionModel.class);

		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(classAttributeAssignment);
		given(classAttributeAssignment.getUnit()).willReturn(unit);
		given(unit.getSystemVersion()).willReturn(classificationSystemVersion);
		given(unit.getUnitType()).willReturn(unitType);
		given(classificationSystemService.getUnitsOfTypeForSystemVersion(classificationSystemVersion, unitType))
				.willReturn(unitsForUnitType);

		// when
		final String referenceFormat = abstractClassificationAttributeTranslator.referenceFormat(excelClassificationAttribute);

		// then
		assertThat(referenceFormat).isEqualTo("value:unit[g,kg]");
	}

	@Test
	public void shouldMultiValueBeSplitNTimesForSubtranslator()
	{
		// given
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(assignment.getMultiValued()).willReturn(true);
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(assignment);

		final ImportParameters imp1 = createImportParameters("imp1", "imp1");
		final ImportParameters imp2 = createImportParameters("imp2", "imp2");
		final ImportParameters imp3 = createImportParameters("imp3", "imp3");

		final ImportParameters imp = mergeImportParametersForMultiValueCase(imp1, imp2, imp3);

		doAnswer(invocationOnMock -> {
			final Serializable cellValue = ((ImportParameters) invocationOnMock.getArguments()[1]).getCellValue();
			return new ImpexValue(cellValue, new ImpexHeaderValue.Builder("any").withUnique(true).build());
		}).when(abstractClassificationAttributeTranslator).importValue(any(), any(), any());

		// when
		final Impex result = abstractClassificationAttributeTranslator.importData(excelClassificationAttribute, imp, null);

		// then
		then(abstractClassificationAttributeTranslator).should(times(3)).importValue(any(), any(), any());
		assertThat(result.getImpexes().get(0).getImpexTable().cellSet()) //
				.hasSize(1) //
				.hasOnlyOneElementSatisfying(cell -> assertThat(cell.getValue()).isEqualTo(imp.getCellValue()));
	}

	@Test
	public void shouldSubtranslatorRetrieveValueWithoutUnit()
	{
		// given
		abstractClassificationAttributeTranslator.setExcelParserSplitter(new UnitExcelParserSplitter());

		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(assignment.getUnit()).willReturn(mock(ClassificationAttributeUnitModel.class));
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(assignment);

		final String cellValue = "abc";
		final String unit = "kg";
		final String cellValueWithUnit = cellValue + ExcelTemplateConstants.REFERENCE_PATTERN_SEPARATOR + unit;
		final ImportParameters importParameters = createImportParameters(cellValueWithUnit, cellValueWithUnit);

		doAnswer(invocationOnMock -> {
			final Serializable val = ((ImportParameters) invocationOnMock.getArguments()[1]).getCellValue();
			return new ImpexValue(val, new ImpexHeaderValue.Builder("any").withUnique(true).build());
		}).when(abstractClassificationAttributeTranslator).importSingle(any(), any(), any());

		// when
		final ImpexValue result = abstractClassificationAttributeTranslator.importValue(excelClassificationAttribute,
				importParameters, null);

		// then
		then(abstractClassificationAttributeTranslator).should().importSingle(any(), argThat(new ArgumentMatcher<ImportParameters>()
		{
			@Override
			public boolean matches(final Object o)
			{
				final String inputValue = String.valueOf(((ImportParameters) o).getCellValue());
				return StringUtils.equals(inputValue, cellValue);
			}
		}), any());
		assertThat(result.getValue()).isEqualTo(cellValueWithUnit);
	}

	protected ImportParameters createImportParameters(final String cellValue, final String rawValue)
	{
		return new ImportParameters(null, null, cellValue, null,
				Lists.newArrayList(ImmutableMap.of(ImportParameters.RAW_VALUE, rawValue)));
	}

	protected ImportParameters mergeImportParametersForMultiValueCase(final ImportParameters... importParameters)
	{
		return Stream.of(importParameters).reduce((imp1, imp2) -> {
			final String cellValue = imp1.getCellValue() + ExcelTemplateConstants.MULTI_VALUE_DELIMITER + imp2.getCellValue();
			final List<Map<String, String>> params = ListUtils.union(imp1.getMultiValueParameters(), imp2.getMultiValueParameters());
			return new ImportParameters(null, null, cellValue, null, params);
		}).get();
	}

	protected ClassificationAttributeUnitModel createUnit(final String code, final double conversionFactor)
	{
		final ClassificationAttributeUnitModel unit = mock(ClassificationAttributeUnitModel.class);
		given(unit.getCode()).willReturn(code);
		given(unit.getConversionFactor()).willReturn(conversionFactor);
		return unit;
	}
}
