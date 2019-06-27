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
package de.hybris.platform.assistedservicewebservices.utils;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;

import java.util.List;


/**
 * Utility class for building Pagination data.
 */
public class PaginationUtils
{

	private PaginationUtils()
	{
	}

	public static PageableData createPageableData(final int currentPage, final int pageSize, final String sort)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);
		return pageableData;
	}

	public static PaginationData buildPaginationData(final PageableData pageableData, final List data)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(pageableData.getCurrentPage());
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setSort(pageableData.getSort());
		paginationData.setTotalNumberOfResults(data.size());
		paginationData.setNumberOfPages((int) Math.ceil((double)data.size() / pageableData.getPageSize()) + 1);
		return paginationData;
	}
}
