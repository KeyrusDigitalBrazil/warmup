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
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;

import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;


public class ClassificationSystemVersionPopulatorTest
{
	ClassificationSystemVersionPopulator populator = new ClassificationSystemVersionPopulator();

	@Test
	public void shouldGetCategorySystemId()
	{
		// given
		final ClassificationSystemVersionModel systemVersion = mock(ClassificationSystemVersionModel.class);
		given(systemVersion.getVersion()).willReturn("version");

		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(assignment.getSystemVersion()).willReturn(systemVersion);

		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		given(attribute.getAttributeAssignment()).willReturn(assignment);

		// when
		final String result = populator.apply(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// then
		assertThat(result).isEqualTo("version");
	}
}
