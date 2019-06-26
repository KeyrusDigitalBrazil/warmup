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
package com.hybris.backoffice.excel.export.wizard.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.classification.ExcelClassificationAttributeFactory;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.translators.ExcelAttributeTranslatorRegistry;


@RunWith(MockitoJUnitRunner.class)
public class ExcelSupportedClassificationAttributesPredicateTest
{
	@Mock
	ExcelAttributeTranslatorRegistry mockedExcelAttributeTranslatorRegistry;
	@Mock
	ExcelClassificationAttributeFactory mockedExcelClassificationAttributeFactory;
	@InjectMocks
	ExcelSupportedClassificationAttributesPredicate excelSupportedClassificationAttributesPredicate;

	@Test
	public void shouldSupportClassificationAttributePresentInRegistry()
	{
		// given
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		final ExcelClassificationAttribute classificationAttribute = mock(ExcelClassificationAttribute.class);

		given(mockedExcelClassificationAttributeFactory.create(assignment)).willReturn(classificationAttribute);
		given(mockedExcelAttributeTranslatorRegistry.canHandle(classificationAttribute)).willReturn(true);

		// when
		final boolean result = excelSupportedClassificationAttributesPredicate.test(assignment);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotSupportClassificationAttributeAbsentInRegistry()
	{
		// given
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		final ExcelClassificationAttribute classificationAttribute = mock(ExcelClassificationAttribute.class);

		given(mockedExcelClassificationAttributeFactory.create(assignment)).willReturn(classificationAttribute);
		given(mockedExcelAttributeTranslatorRegistry.canHandle(classificationAttribute)).willReturn(false);

		// when
		final boolean result = excelSupportedClassificationAttributesPredicate.test(assignment);

		// then
		assertThat(result).isFalse();
	}
}
