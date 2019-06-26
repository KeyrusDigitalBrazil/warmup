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


public class ClassificationAttributeLocLangPopulatorTest
{
	ClassificationAttributeLocLangPopulator populator = new ClassificationAttributeLocLangPopulator();

	@Test
	public void shouldGetEnglishLocLang()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		given(attribute.isLocalized()).willReturn(true);
		given(attribute.getIsoCode()).willReturn("en");

		// when
		final String result = populator.apply(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// then
		assertThat(result).isEqualTo("en");
	}

	@Test
	public void shouldGetFrenchLocLang()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		given(attribute.isLocalized()).willReturn(true);
		given(attribute.getIsoCode()).willReturn("fr");

		// when
		final String result = populator.apply(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// then
		assertThat(result).isEqualTo("fr");
	}
}
