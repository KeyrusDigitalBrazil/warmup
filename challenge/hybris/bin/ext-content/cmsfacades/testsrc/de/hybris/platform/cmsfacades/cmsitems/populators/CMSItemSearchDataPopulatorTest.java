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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemSearchDataPopulatorTest
{
	@Spy
	@InjectMocks
	private CMSItemSearchDataPopulator populator;

	@Test
	public void testWhenSingleParamsValid()
	{
		// GIVEN
		final CMSItemSearchData source = new CMSItemSearchData();
		source.setItemSearchParams("label:test");

		final de.hybris.platform.cms2.data.CMSItemSearchData target = new de.hybris.platform.cms2.data.CMSItemSearchData();

		// WHEN
		populator.populate(source, target);

		// THEN
		assertThat(target.getItemSearchParams().size(), is(1));
		assertThat(target.getItemSearchParams().get("label"), is("test"));
	}

	@Test
	public void testWhenMultipleParamsValid()
	{
		// GIVEN
		final CMSItemSearchData source = new CMSItemSearchData();
		source.setItemSearchParams("label:test,title:123,header:abc");

		final de.hybris.platform.cms2.data.CMSItemSearchData target = new de.hybris.platform.cms2.data.CMSItemSearchData();

		// WHEN
		populator.populate(source, target);

		// THEN
		assertThat(target.getItemSearchParams().size(), is(3));
		assertThat(target.getItemSearchParams().get("label"), is("test"));
		assertThat(target.getItemSearchParams().get("title"), is("123"));
		assertThat(target.getItemSearchParams().get("header"), is("abc"));
	}

	@Test(expected = ConversionException.class)
	public void testWhenItemSearchParamsInvalid()
	{
		// GIVEN
		final CMSItemSearchData source = new CMSItemSearchData();
		source.setItemSearchParams("label:123,test:,:,:,label:456");

		final de.hybris.platform.cms2.data.CMSItemSearchData target = new de.hybris.platform.cms2.data.CMSItemSearchData();

		// WHEN
		populator.populate(source, target);
	}

	@Test
	public void testBuildTypeCodesListMultiTypeCodes()
	{
		final List<String> typeCodes = populator.buildTypeCodesList("type1, type2");

		MatcherAssert.assertThat(typeCodes.size(), is(2));
		MatcherAssert.assertThat(typeCodes.get(0), equalTo("type1"));
		MatcherAssert.assertThat(typeCodes.get(1), equalTo("type2"));
	}

	@Test
	public void testBuildTypeCodesListWithNullTypeCode()
	{
		final List<String> typeCodes = populator.buildTypeCodesList(null);

		MatcherAssert.assertThat(typeCodes.isEmpty(), is(true));
	}
}
