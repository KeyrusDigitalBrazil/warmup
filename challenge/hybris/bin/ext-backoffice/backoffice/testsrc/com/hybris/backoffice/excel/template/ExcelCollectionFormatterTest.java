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
package com.hybris.backoffice.excel.template;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;


public class ExcelCollectionFormatterTest
{
	CollectionFormatter excelCollectionFormatter = new ExcelCollectionFormatter();

	@Test
	public void shouldFormatToCollection()
	{
		// when
		final Collection<String> elements = excelCollectionFormatter.formatToCollection("{one},{two},{three},{four}");

		// then
		assertThat(elements).containsOnly("one", "two", "three", "four");
	}

	@Test
	public void shouldFormatToString()
	{
		// given
		final List<String> objects = Arrays.asList("apple", "orange", "pineapple");

		// when
		final String result = excelCollectionFormatter.formatToString(objects);

		// then
		assertThat(result).isEqualTo("{apple},{orange},{pineapple}");
	}

	@Test
	public void shouldFormatToStringFromVarArgs()
	{
		// when
		final String result = excelCollectionFormatter.formatToString("one", "two", "three");

		// then
		assertThat(result).isEqualTo("{one},{two},{three}");
	}

	@Test
	public void shouldFormatToCollectionElementsWithCommas()
	{
		// when
		final Set<String> result = excelCollectionFormatter.formatToCollection("{one,two},{three,four}");

		// then
		assertThat(result).containsOnly("one,two", "three,four");
	}
}
