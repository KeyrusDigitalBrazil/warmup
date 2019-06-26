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

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDataToAttributeAttributeContentConverterTest
{
	private DefaultDataToAttributeContentConverter converter = new DefaultDataToAttributeContentConverter();

	@Test
	public void shouldThrowsInvalidArgumentException()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void whenPrimitiveTypeIsStringReturnsStringAsIs()
	{
		converter.setPrimitiveType(String.class);
		final String object = "someString";
		assertThat(converter.convert(object), is(object));
	}

	@Test
	public void whenPrimitiveTypeIsBooleanReturnsMatchingBoolean()
	{
		converter.setPrimitiveType(Boolean.class);
		final Boolean object = Boolean.TRUE;
		assertThat(converter.convert(object), is(true));
	}

	@Test
	public void whenPrimitiveTypeIsBooleanStringReturnsMatchingBoolean()
	{
		converter.setPrimitiveType(Boolean.class);
		final String object = "true";
		assertThat(converter.convert(object), is(true));
	}

	@Test
	public void whenPrimitiveTypeIsSmallBooleanReturnsMatchingBoolean()
	{
		converter.setPrimitiveType(Boolean.class);
		final boolean object = true;
		assertThat(converter.convert(object), is(true));
	}

	
	@Test
	public void whenPrimitiveTypeIsFloatReturnsMatchingFloat()
	{
		converter.setPrimitiveType(Float.class);
		final String object = "123.45";
		assertThat(converter.convert(object), is(123.45F));
	}

	@Test
	public void whenPrimitiveTypeIsLongReturnsMatchingLong()
	{
		converter.setPrimitiveType(Long.class);
		final String object = "1234567890";
		assertThat(converter.convert(object), is(1234567890L));
	}

	@Test
	public void whenPrimitiveTypeIsIntegerReturnsMatchingInteger()
	{
		converter.setPrimitiveType(Integer.class);
		final String object = "123";
		assertThat(converter.convert(object), is(123));
	}

}
