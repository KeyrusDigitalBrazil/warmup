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
package de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.impl;


import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SearchQueryTemplateNameResolver;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;


/**
 * Default implementation of {@link SearchQueryTemplateNameResolver}.
 */
public class DefaultSearchQueryTemplateNameResolver implements SearchQueryTemplateNameResolver
{
	@Override
	public String resolveTemplateName(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType,
			final SearchQueryContext searchQueryContext)
	{
		if (searchQueryContext != null)
		{
			return searchQueryContext.getCode().toUpperCase();
		}

		return SearchQueryContext.DEFAULT.getCode();
	}
}
