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

import static com.hybris.backoffice.excel.data.ImportParameters.RAW_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.Ordered;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImpexValue;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.ExcelImportContext;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;
import com.hybris.backoffice.excel.translators.generic.RequiredAttribute;
import com.hybris.backoffice.excel.translators.generic.factory.ExportDataFactory;
import com.hybris.backoffice.excel.translators.generic.factory.ReferenceFormatFactory;
import com.hybris.backoffice.excel.translators.generic.factory.RequiredAttributesFactory;


@RunWith(MockitoJUnitRunner.class)
public class ExcelClassificationReferenceTranslatorTest
{
	@Mock
	ExportDataFactory mockedExportDataFactory;
	@Mock
	ReferenceFormatFactory mockedReferenceFormatFactory;
	@Mock
	RequiredAttributesFactory mockedRequiredAttributesFactory;
	@Mock
	ClassificationAttributeHeaderValueCreator mockedHeaderValueCreator;
	@Mock
	ExcelFilter<AttributeDescriptorModel> filter;

	@InjectMocks
	ExcelClassificationReferenceTranslator excelClassificationReferenceTranslator;

	@Test
	public void shouldExportSingleReferenceAttributeValue()
	{
		// given
		final ComposedTypeModel referenceType = mock(ComposedTypeModel.class);

		final ExcelClassificationAttribute excelAttribute = prepareExcelAttribute(referenceType);

		final RequiredAttribute requiredAttribute = mock(RequiredAttribute.class);
		given(mockedRequiredAttributesFactory.create(referenceType)).willReturn(requiredAttribute);

		final String objectToExport = "value";
		final FeatureValue featureToExport = mock(FeatureValue.class);
		given(featureToExport.getValue()).willReturn(objectToExport);
		given(mockedExportDataFactory.create(requiredAttribute, objectToExport)).willReturn(Optional.of("result"));

		// when
		final Optional<String> result = excelClassificationReferenceTranslator.exportSingle(excelAttribute, featureToExport);

		// then
		then(mockedExportDataFactory).should().create(requiredAttribute, objectToExport);
		assertThat(result).isPresent();
		assertThat(result).hasValue("result");
	}

	@Test
	public void shouldGetSingleReferenceFormat()
	{
		// given
		final ComposedTypeModel referenceType = mock(ComposedTypeModel.class);
		final ExcelClassificationAttribute excelAttribute = prepareExcelAttribute(referenceType);

		final RequiredAttribute requiredAttribute = mock(RequiredAttribute.class);
		given(mockedRequiredAttributesFactory.create(referenceType)).willReturn(requiredAttribute);

		final String referenceFormat = "referenceFormat";
		given(mockedReferenceFormatFactory.create(requiredAttribute)).willReturn(referenceFormat);

		// when
		final String result = excelClassificationReferenceTranslator.singleReferenceFormat(excelAttribute);

		// then
		then(mockedReferenceFormatFactory).should().create(requiredAttribute);
		assertThat(result).isEqualTo(referenceFormat);
	}

	@Test
	public void shouldImportSingleReferenceAttributeValue()
	{
		// given
		final ComposedTypeModel referenceType = mock(ComposedTypeModel.class);
		final ExcelClassificationAttribute excelAttribute = prepareExcelAttribute(referenceType);
		final ExcelImportContext importContext = mock(ExcelImportContext.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		given(importParameters.getSingleValueParameters()).willReturn(Maps.newHashMap(RAW_VALUE, "cellValue"));
		given(importParameters.getIsoCode()).willReturn("isoCode");
		given(mockedHeaderValueCreator.create(excelAttribute, importContext)).willReturn("headerValue");

		// when
		final ImpexValue impexValue = excelClassificationReferenceTranslator.importSingle(excelAttribute, importParameters,
				importContext);

		// then
		assertThat(impexValue).isNotNull();
		assertThat(impexValue.getValue()).isEqualTo("cellValue");
		assertThat(impexValue.getHeaderValue()).isNotNull();
		assertThat(impexValue.getHeaderValue().getName()).isEqualTo("headerValue");
		assertThat(impexValue.getHeaderValue().getLang()).isEqualTo("isoCode");
		assertThat(impexValue.getHeaderValue().isUnique()).isEqualTo(false);
	}

	@Test
	public void shouldHandleReferenceTypesWhenComposedTypeHasAtLeastOneUniqueAttribute()
	{
		// given
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignment.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.REFERENCE);
		given(classAttributeAssignment.getReferenceType()).willReturn(composedTypeModel);
		given(composedTypeModel.getDeclaredattributedescriptors()).willReturn(Arrays.asList(attributeDescriptorModel));
		given(composedTypeModel.getInheritedattributedescriptors()).willReturn(Collections.emptyList());
		given(filter.test(attributeDescriptorModel)).willReturn(true);
		final ExcelClassificationAttribute referenceExcelAttribute = mock(ExcelClassificationAttribute.class);
		given(referenceExcelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignment);

		// when
		final boolean result = excelClassificationReferenceTranslator.canHandle(referenceExcelAttribute);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotHandleReferenceTypesWhenComposedTypeDoesNotHaveAnyUniqueAttribute()
	{
		// given
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignment.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.REFERENCE);
		given(classAttributeAssignment.getReferenceType()).willReturn(composedTypeModel);
		given(composedTypeModel.getDeclaredattributedescriptors()).willReturn(Arrays.asList(attributeDescriptorModel));
		given(composedTypeModel.getInheritedattributedescriptors()).willReturn(Collections.emptyList());
		given(filter.test(attributeDescriptorModel)).willReturn(false);
		final ExcelClassificationAttribute referenceExcelAttribute = mock(ExcelClassificationAttribute.class);
		given(referenceExcelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignment);

		// when
		final boolean result = excelClassificationReferenceTranslator.canHandle(referenceExcelAttribute);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldNotHandleOtherAttributeTypesThanReference()
	{
		// given
		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignment.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.NUMBER);

		final ExcelClassificationAttribute numberExcelAttribute = mock(ExcelClassificationAttribute.class);
		given(numberExcelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignment);

		// when
		final boolean result = excelClassificationReferenceTranslator.canHandle(numberExcelAttribute);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldOrderHaveADefaultValueOfLowestPrecedenceMinusHundred()
	{
		// when
		final int order = excelClassificationReferenceTranslator.getOrder();

		// then
		assertThat(order).isEqualTo(Ordered.LOWEST_PRECEDENCE - 100);
	}

	@Test
	public void shouldOrderByInjectableByProperty()
	{
		// given
		final int givenOrder = 100;

		// when
		excelClassificationReferenceTranslator.setOrder(givenOrder);

		// then
		assertThat(excelClassificationReferenceTranslator.getOrder()).isEqualTo(givenOrder);
	}

	private ExcelClassificationAttribute prepareExcelAttribute(final ComposedTypeModel referenceType)
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignment.getReferenceType()).willReturn(referenceType);

		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignment);
		return excelAttribute;
	}
}
