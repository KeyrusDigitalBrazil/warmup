/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAttributeToDataAttributeContentConverterTest
{
	private final DefaultAttributeToDataContentConverter converter = new DefaultAttributeToDataContentConverter();

	@Test
	public void shouldThrowsInvalidArgumentException()
	{
		final Object value = converter.convert(null);
		assertThat(value, nullValue());
	}

	@Test
	public void shouldInvokeToString()
	{
		final String object  = "VALUE";
		final Object value = converter.convert(object);
		assertThat(value, is(object));
	}
}
