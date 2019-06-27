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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

/**
 * Category name value provider. Value provider that generates field values for localized category names.
 */
public class CategoryNameValueProvider extends CategoryCodeValueProvider
{
	@Override
	protected Object getPropertyValue(final Object model)
	{
		return getPropertyValue(model, "name");
	}
}
