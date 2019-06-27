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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import org.junit.Test;


public class ReadableCheckingFilterTest
{

	private final ReadableCheckingFilter filter = new ReadableCheckingFilter();

	@Test
	public void shouldFilterOutNotReadableAttributes()
	{
		// given
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(attributeDescriptorModel.getReadable()).willReturn(false);

		// when
		final boolean result = filter.test(attributeDescriptorModel);

		// then
		assertThat(result).isFalse();
	}

}
