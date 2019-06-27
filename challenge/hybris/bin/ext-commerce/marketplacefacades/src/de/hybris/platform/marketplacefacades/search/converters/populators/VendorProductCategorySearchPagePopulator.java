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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.converters.populator.ProductCategorySearchPagePopulator;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;

import java.util.stream.Collectors;


/**
 * A populator for populating search result.
 */
public class VendorProductCategorySearchPagePopulator<QUERY, STATE, RESULT, ITEM extends ProductData, SCAT, CATEGORY> //NOSONAR
		extends
		ProductCategorySearchPagePopulator<QUERY, STATE, RESULT, ProductData, SCAT, CATEGORY>
{

	@Override
	public void populate(final ProductCategorySearchPageData<QUERY, RESULT, SCAT> source,
			final ProductCategorySearchPageData<STATE, ProductData, CATEGORY> target)
	{
		source.setFacets(source.getFacets().stream().filter(facet -> "category".equalsIgnoreCase(facet.getCode()))
				.collect(Collectors.toList()));
		super.populate(source, target);
	}
}
