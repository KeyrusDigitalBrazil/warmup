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
package de.hybris.platform.notificationfacades.populators;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationfacades.data.SiteMessageData;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SiteMessageSearchPageDataPopulatorTest
{

	private SiteMessageSearchPageDataPopulator populator;

	@Mock
	private SearchPageData<SiteMessageForCustomerModel> source;

	@Mock
	private List<SiteMessageForCustomerModel> messageModels;

	@Mock
	private List<SiteMessageData> messageData;

	@Mock
	private PaginationData paginationData;

	@Mock
	private Converter<SiteMessageForCustomerModel, SiteMessageData> siteMessageConverter;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		populator = new SiteMessageSearchPageDataPopulator();
		populator.setSiteMessageConverter(siteMessageConverter);

		source.setResults(messageModels);
		source.setPagination(paginationData);
		source.setSorts(Collections.EMPTY_LIST);

	}

	@Test
	public void testPopulate()
	{
		final SearchPageData<SiteMessageData> target = new SearchPageData();
		when(siteMessageConverter.convertAll(messageModels)).thenReturn(messageData);
		when(source.getPagination()).thenReturn(paginationData);
		when(source.getResults()).thenReturn(messageModels);
		when(source.getSorts()).thenReturn(Collections.EMPTY_LIST);

		populator.populate(source, target);

		Assert.assertEquals(messageData, target.getResults());
		Assert.assertEquals(paginationData, target.getPagination());
		Assert.assertEquals(Collections.EMPTY_LIST, target.getSorts());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateSourceNull()
	{
		final SearchPageData<SiteMessageData> target = new SearchPageData();
		populator.populate(null, target);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateTargetNull()
	{
		populator.populate(source, null);

	}

}
