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
package com.hybris.backoffice.excel.template.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;


public class ClassificationClassPopulatorTest
{
	ClassificationClassPopulator populator = new ClassificationClassPopulator();

	@Test
	public void shouldGetClassificationClassName()
	{
		// given
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		given(classificationClass.getCode()).willReturn("classificationClassName");

		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(assignment.getClassificationClass()).willReturn(classificationClass);

		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		given(attribute.getAttributeAssignment()).willReturn(assignment);

		// when
		final String result = populator.apply(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// then
		assertThat(result).isEqualTo("classificationClassName");
	}
}
