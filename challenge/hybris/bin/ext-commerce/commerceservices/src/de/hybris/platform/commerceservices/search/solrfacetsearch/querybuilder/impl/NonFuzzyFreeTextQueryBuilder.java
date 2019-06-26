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
package de.hybris.platform.commerceservices.search.solrfacetsearch.querybuilder.impl;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of FreeTextQueryBuilder provides for simple querying of a property.
 *
 * @deprecated Since 6.4, default search mode (instead of legacy) should be used.
 */
@Deprecated
public class NonFuzzyFreeTextQueryBuilder extends AbstractNonFuzzyTextQueryBuilder
{
	private String propertyName;
	private int boost;

	protected String getPropertyName()
	{
		return propertyName;
	}

	@Required
	public void setPropertyName(final String propertyName)
	{
		this.propertyName = propertyName;
	}

	protected int getBoost()
	{
		return boost;
	}

	@Required
	public void setBoost(final int boost)
	{
		this.boost = boost;
	}

	@Override
	public void addFreeTextQuery(final SearchQuery searchQuery, final String fullText, final String[] textWords)
	{
		final IndexedProperty indexedProperty = searchQuery.getIndexedType().getIndexedProperties().get(getPropertyName());
		if (indexedProperty != null)
		{
			addFreeTextQuery(searchQuery, indexedProperty, fullText, textWords, getBoost());
		}
	}
}
