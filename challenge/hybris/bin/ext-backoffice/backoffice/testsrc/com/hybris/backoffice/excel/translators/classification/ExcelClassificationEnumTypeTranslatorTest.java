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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.enums.TestEnum;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImpexValue;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.ExcelImportContext;


@RunWith(MockitoJUnitRunner.class)
public class ExcelClassificationEnumTypeTranslatorTest
{

	@Mock
	private ClassificationService classificationService;
	@Mock
	private ClassificationAttributeHeaderValueCreator classificationAttributeHeaderValueCreator;
	@InjectMocks
	private ExcelClassificationEnumTypeTranslator translator = new ExcelClassificationEnumTypeTranslator();

	@Test
	public void shouldOrderParamBeInjectable()
	{
		// when
		translator.setOrder(1337);
		// then
		assertThat(translator.getOrder()).isEqualTo(1337);
	}

	@Test
	public void shouldImportValueWithClassificationAttributeHeaderCreator()
	{
		// given
		final ExcelClassificationAttribute excelClassificationAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		given(importParameters.getCellValue()).willReturn(TestEnum.TESTVALUE1);
		given(importParameters.getIsoCode()).willReturn("isoCode");
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(mock(ClassAttributeAssignmentModel.class));
		given(classificationAttributeHeaderValueCreator.create(eq(excelClassificationAttribute), any())).willReturn("headerValue");
		// when
		final ImpexValue result = translator.importValue(excelClassificationAttribute, importParameters, new ExcelImportContext());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getValue()).isEqualTo(TestEnum.TESTVALUE1);
		assertThat(result.getHeaderValue()) //
				.isNotNull() //
				.hasFieldOrPropertyWithValue("name", "headerValue") //
				.hasFieldOrPropertyWithValue("lang", "isoCode") //
				.hasFieldOrPropertyWithValue("unique", false);
	}

	@Test
	public void shouldTranslatorBeHandledWhenAttributeTypeIsEnum()
	{
		// given
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(assignment.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.ENUM);
		final ExcelClassificationAttribute excelClassificationAttribute = new ExcelClassificationAttribute();
		excelClassificationAttribute.setAttributeAssignment(assignment);

		// when - then
		assertTrue(translator.canHandle(excelClassificationAttribute));
	}

	@Test
	public void shouldTranslatorNotBeHandledWhenAttributeTypeIsNotEnum()
	{
		// given
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(assignment.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.NUMBER);
		final ExcelClassificationAttribute excelClassificationAttribute = new ExcelClassificationAttribute();
		excelClassificationAttribute.setAttributeAssignment(assignment);

		// when - then
		assertFalse(translator.canHandle(excelClassificationAttribute));
	}

	@Test
	public void shouldExportEnumValueCorrectly()
	{
		// given
		final TestEnum testEnum = TestEnum.TESTVALUE1;
		final FeatureValue featureValue = mock(FeatureValue.class);
		given(featureValue.getValue()).willReturn(testEnum);

		// when
		final Optional<String> exportedValue = translator.exportSingle(mock(ExcelClassificationAttribute.class), featureValue);

		// then
		assertTrue(exportedValue.isPresent());
		assertThat(exportedValue.get()).isEqualTo(testEnum.getCode());
	}

	@Test
	public void shouldExportClassificationAttributeValue()
	{
		// given
		final ClassificationAttributeValueModel testEnum = mock(ClassificationAttributeValueModel.class);
		given(testEnum.getCode()).willReturn("testValue1");
		final FeatureValue featureValue = mock(FeatureValue.class);
		given(featureValue.getValue()).willReturn(testEnum);

		// when
		final Optional<String> exportedValue = translator.exportSingle(mock(ExcelClassificationAttribute.class), featureValue);

		// then
		assertTrue(exportedValue.isPresent());
		assertThat(exportedValue.get()).isEqualTo(testEnum.getCode());
	}
}
