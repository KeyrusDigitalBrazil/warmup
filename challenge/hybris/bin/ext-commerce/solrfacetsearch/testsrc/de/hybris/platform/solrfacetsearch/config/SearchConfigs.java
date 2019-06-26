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
package de.hybris.platform.solrfacetsearch.config;

import java.util.List;


public final class SearchConfigs
{
	// Suppresses default constructor, ensuring non-instantiability.
	private SearchConfigs()
	{
	}

	public static SearchConfig createSearchConfig(final List emptyList, final int i)
	{
		final SearchConfig config = new SearchConfig();
		config.setDefaultSortOrder(emptyList);
		config.setPageSize(i);
		return config;
	}

}
