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

import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.log4j.Logger;




/**
 * Utility class for processing pageable flexible search results page wise.
 */
public class ProductConfigurationPagingUtil
{

	public static final int MAXIMUM_PAGES = 10000;
	public static final int PAGE_SIZE = 100;

	private static final Logger LOG = Logger.getLogger(ProductConfigurationPagingUtil.class);

	/**
	 * @param searchFunction
	 *           function to deliver pageable results
	 * @param consumer
	 *           consumer for processing the results page wise
	 */
	public <T> void processPageWise(final Function<Integer, SearchPageData<T>> searchFunction,
			final Consumer<List<T>> searchResultConsumer)
	{
		int currentPage = 0;
		boolean hasTotalResultSizeBeenReached;
		do
		{
			hasTotalResultSizeBeenReached = processPage(searchFunction, currentPage, searchResultConsumer);
			currentPage++;
		}
		while (!hasTotalResultSizeBeenReached && currentPage < MAXIMUM_PAGES);

	}

	protected <T> boolean processPage(final Function<Integer, SearchPageData<T>> searchFunction, final int currentPage,
			final Consumer<List<T>> searchResultConsumer)
	{
		final SearchPageData<T> searchPageData = searchFunction.apply(currentPage);
		LOG.info(String.format("Processing search result page %s of %s", searchPageData.getPagination().getCurrentPage() + 1,
				searchPageData.getPagination().getNumberOfPages()));
		searchResultConsumer.accept(searchPageData.getResults());
		return searchPageData.getResults().size() < PAGE_SIZE;
	}
}
