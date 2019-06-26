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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 */
@UnitTest
public class ProductConfigurationPagingUtilTest
{
	private final ProductConfigurationPagingUtil classUndertest = new ProductConfigurationPagingUtil();

	@Test
	public void testCleanUpConfigMaxPages()
	{
		final SearchPageData<ProductConfigurationModel> fullSearchPageData = mockFullSearchPage();
		classUndertest.processPageWise(currentPage -> dummySearchReturn(fullSearchPageData), list -> dummyRelease(list));
		assertTrue("cleanUp should terminate after max pages", true);
	}


	private void dummyRelease(final List<ProductConfigurationModel> list)
	{
		//do nothing
	}

	protected SearchPageData<ProductConfigurationModel> dummySearchReturn(final SearchPageData<ProductConfigurationModel> result)
	{
		return result;
	}

	protected SearchPageData<ProductConfigurationModel> mockFullSearchPage()
	{
		final SearchPageData<ProductConfigurationModel> fullSearchPageData = new SearchPageData<>();
		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(ProductConfigurationPagingUtil.PAGE_SIZE * ProductConfigurationPagingUtil.MAXIMUM_PAGES);
		pagination.setNumberOfPages(ProductConfigurationPagingUtil.MAXIMUM_PAGES);
		fullSearchPageData.setPagination(pagination);
		final List<ProductConfigurationModel> fullList = Mockito.mock(ArrayList.class);
		fullSearchPageData.setResults(fullList);
		given(fullList.size()).willReturn(ProductConfigurationPagingUtil.PAGE_SIZE);
		return fullSearchPageData;
	}


}
