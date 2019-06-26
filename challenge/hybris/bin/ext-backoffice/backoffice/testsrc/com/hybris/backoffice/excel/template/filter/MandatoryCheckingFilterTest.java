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
package com.hybris.backoffice.excel.template.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MandatoryCheckingFilterTest
{

	private MandatoryCheckingFilter filter = new MandatoryCheckingFilter();

	@Test
	public void shouldReturnFalseWhenAttributeDescriptorIsOptional()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		given(model.getOptional()).willReturn(true);
		given(model.getPrivate()).willReturn(false);

		// when
		final boolean result = filter.test(model);

		// then
		assertFalse(result);
	}

	@Test
	public void shouldReturnTrueWhenAttributeDescriptorIsNotOptional()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		given(model.getOptional()).willReturn(false);
		given(model.getPrivate()).willReturn(false);

		// when
		final boolean result = filter.test(model);

		// then
		assertTrue(result);
	}

}
