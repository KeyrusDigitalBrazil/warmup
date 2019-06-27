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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider;

import de.hybris.platform.solrfacetsearch.provider.FacetTopValuesProvider;


/**
 * Top Values are a list of facet values that are immediately shown on search and category pages for facets with many
 * values. Other values will be collapsed.
 *
 * @deprecated Since 6.3, replaced by {@link FacetTopValuesProvider}
 */
@Deprecated
public interface TopValuesProvider extends FacetTopValuesProvider
{
	// Empty interface
}