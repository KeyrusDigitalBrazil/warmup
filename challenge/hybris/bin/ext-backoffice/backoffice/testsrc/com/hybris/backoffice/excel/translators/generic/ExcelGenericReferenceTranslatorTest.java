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
package com.hybris.backoffice.excel.translators.generic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.Ordered;


@RunWith(MockitoJUnitRunner.class)
public class ExcelGenericReferenceTranslatorTest
{

	@InjectMocks
	private ExcelGenericReferenceTranslator excelGenericReferenceTranslator;

	@Test
	public void shouldUseDefaultOrderWhenAnotherOrderIsNotSet()
	{
		// given

		// when
		final int order = excelGenericReferenceTranslator.getOrder();

		// then
		assertThat(order).isEqualTo(Ordered.LOWEST_PRECEDENCE - 100);
	}

	@Test
	public void shouldOverrideDefaultOrder()
	{
		// given
		excelGenericReferenceTranslator.setOrder(100);

		// when
		final int order = excelGenericReferenceTranslator.getOrder();

		// then
		assertThat(order).isEqualTo(100);
	}
}
