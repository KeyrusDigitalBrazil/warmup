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
package de.hybris.platform.customerticketingc4cintegration.facade;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Utility class C4C Facade
 *
 */
public class C4CBaseFacade
{
	private static final String BY_LAST_CHANGE_DATE_TIME = "LastChangeDateTime";
	private static final String BY_TICKET_ID = "ObjectID";

	protected <S, T> SearchPageData<T> convertPageData(final List<S> source, final Converter<S, T> converter,
			final PageableData pageableData, int total)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setSorts(this.getSupportedSortsForC4C(pageableData));
		final PaginationData pagination = new PaginationData();

		// Calculate the number of pages
		pagination.setNumberOfPages((int) Math.ceil(((double) total) / pageableData.getPageSize()));

		// Work out the current page, fixing any invalid page values
		pagination.setCurrentPage(Math.max(0, Math.min(pagination.getNumberOfPages(), pageableData.getCurrentPage())));

		pagination.setTotalNumberOfResults(total);
		pagination.setCurrentPage(pageableData.getCurrentPage());
		pagination.setPageSize(pageableData.getPageSize());
		pagination.setSort(pageableData.getSort());
		result.setPagination(pagination);
		result.setResults(Converters.convertAll(source, converter));
		return result;
	}

	protected List<SortData> getSupportedSortsForC4C(final PageableData pageableData)
	{
		final List<SortData> sorts = new ArrayList<SortData>();
		final SortData byDate = new SortData();
		byDate.setCode(BY_LAST_CHANGE_DATE_TIME);
		byDate.setSelected(true);
		sorts.add(byDate);

		final SortData byTicketId = new SortData();
		byTicketId.setCode(BY_TICKET_ID);
		sorts.add(byTicketId);

		if (StringUtils.isEmpty(pageableData.getSort()) || BY_LAST_CHANGE_DATE_TIME.equals(pageableData.getSort()))
		{
			byDate.setSelected(true);
		}
		else
		{
			byTicketId.setSelected(true);
		}
		return sorts;
	}
}
