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
package de.hybris.platform.acceleratorservices.search.comparators;

import de.hybris.platform.solrfacetsearch.search.FacetValue;

import java.util.Comparator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * FacetValue name comparator. Uses an injected name comparator.
 */
public class FacetValueNameComparator implements Comparator<FacetValue>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(FacetValueNameComparator.class);

	private Comparator<String> comparator;

	@Override
	public int compare(final FacetValue value1, final FacetValue value2)
	{
		if (value1 == null || value2 == null)
		{
			return 0;
		}
		return getComparator().compare(value1.getName(), value2.getName());
	}

	protected Comparator<String> getComparator()
	{
		return comparator;
	}

	@Required
	public void setComparator(final Comparator<String> comparator)
	{
		this.comparator = comparator;
	}
}
