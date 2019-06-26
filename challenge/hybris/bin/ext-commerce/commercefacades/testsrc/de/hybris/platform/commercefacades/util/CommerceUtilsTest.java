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
package de.hybris.platform.commercefacades.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


/**
 * Unit tests for {@link CommerceUtils}.
 */
@UnitTest
public class CommerceUtilsTest
{

	@Test
	public void testConvertPageData()
	{
		final SearchPageData<String> input = new SearchPageData<String>();
		final List<String> inputResults = Arrays.asList(new String[]
		{ "1", "2" });
		input.setResults(inputResults);
		input.setPagination(new PaginationData());
		input.setSorts(new ArrayList<SortData>());
		final Converter<String, Integer> converter = new String2Integer();

		final SearchPageData<Integer> output = CommerceUtils.convertPageData(input, converter);

		assertNotNull(output);
		assertEquals(input.getPagination(), output.getPagination());
		assertEquals(input.getSorts(), output.getSorts());
		final List<Integer> outputResults = output.getResults();
		assertNotNull(outputResults);
		assertEquals(inputResults.size(), outputResults.size());
		for (int i = 0; i < outputResults.size(); i++)
		{
			assertEquals(converter.convert(inputResults.get(i)), outputResults.get(i));
		}
	}

	private class String2Integer implements Converter<String, Integer>
	{

		@Override
		public Integer convert(final String arg0) throws ConversionException
		{
			return Integer.valueOf(arg0);
		}

		@Override
		public Integer convert(final String arg0, final Integer arg1) throws ConversionException
		{
			return convert(arg0);
		}
	}
}
