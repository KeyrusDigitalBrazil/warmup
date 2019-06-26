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

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;


/**
 * An populator for setting category data from facet search result.
 */
public class VendorCategoryFacetPopulator implements Populator<FacetData<SearchStateData>, VendorData>
{

	private static final int TOP_LENGTH = 10;

	@Override
	public void populate(final FacetData<SearchStateData> source, final VendorData target)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		final List<FacetValueData<SearchStateData>> facetValues = source.getValues();
		final List<CategoryData> categories = new ArrayList<>();
		if (!CollectionUtils.isEmpty(facetValues))
		{
			facetValues.forEach(val -> {
				final CategoryData category = new CategoryData();
				category.setCode(val.getCode());
				category.setName(val.getName());
				category.setUrl(val.getQuery().getUrl());
				categories.add(category);
			});
		}
		target.setCategories(categories);

		if (categories.size() > TOP_LENGTH)
		{
			target.setTopCategories(categories.subList(0, TOP_LENGTH));
		}
		else
		{
			target.setTopCategories(categories);
		}
	}

}
