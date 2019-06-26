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

import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;


public class ClassificationAttributePopulatorTest
{

	ClassificationAttributePopulator populator = new ClassificationAttributePopulator();

	@Test
	public void shouldGetClassificationAttribute()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		given(attribute.getQualifier()).willReturn("attributeName");

		// when
		final String result = populator.apply(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// then
		assertThat(result).isEqualTo("attributeName");
	}
}
