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
package de.hybris.platform.marketplacefacades.search.converters.populators;

import de.hybris.platform.commercefacades.search.converters.populator.FacetPopulator;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;

import org.springframework.util.Assert;


/**
 * A populator for setting current facets.
 */
public class VendorProductsPageFacetPopulator<QUERY, STATE> extends FacetPopulator<QUERY, STATE>
{

	@Override
	public void populate(final FacetData<QUERY> source, final FacetData<STATE> target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		super.populate(source, target);
	}
}
