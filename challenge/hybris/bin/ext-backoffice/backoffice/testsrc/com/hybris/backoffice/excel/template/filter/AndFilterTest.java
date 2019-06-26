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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AndFilterTest
{

	private final AndFilter<AttributeDescriptorModel> andFilter = new AndFilter<>();
	@Mock
	private UniqueCheckingFilter uniqueCheckingFilter;
	@Mock
	private DefaultValueCheckingFilter defaultValueCheckingFilter;
	final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);

	@Test
	public void shouldReturnTrueWhenAllSubFiltersReturnTrue()
	{
		// given
		given(uniqueCheckingFilter.test(model)).willReturn(true);
		given(defaultValueCheckingFilter.test(model)).willReturn(true);
		andFilter.setFilters(Arrays.asList(uniqueCheckingFilter, defaultValueCheckingFilter));

		// when
		final boolean result = andFilter.test(model);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenNotAllSubFiltersReturnTrue()
	{
		// given
		given(uniqueCheckingFilter.test(model)).willReturn(false);
		given(defaultValueCheckingFilter.test(model)).willReturn(true);
		andFilter.setFilters(Arrays.asList(uniqueCheckingFilter, defaultValueCheckingFilter));

		// when
		final boolean result = andFilter.test(model);

		// then
		assertThat(result).isFalse();
	}
}
