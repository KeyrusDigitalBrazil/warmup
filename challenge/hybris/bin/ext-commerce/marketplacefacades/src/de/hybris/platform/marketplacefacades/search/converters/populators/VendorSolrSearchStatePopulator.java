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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator.SolrSearchStatePopulator;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.url.UrlResolver;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * A populator for setting url of facets.
 */
public class VendorSolrSearchStatePopulator extends SolrSearchStatePopulator
{

	private CommerceCategoryService commerceCategoryService;
	private UrlResolver<CategoryModel> vendorCategoryUrlResolver;

	@Override
	public void populate(final SolrSearchQueryData source, final SearchStateData target)
	{
		final List<SolrSearchQueryTermData> filterTerms = source.getFilterTerms().stream()
				.filter(term -> "category".equalsIgnoreCase(term.getKey())).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(filterTerms))
		{
			final String categoryCode = filterTerms.get(filterTerms.size() - 1).getValue();
			source.setFilterTerms(source.getFilterTerms().stream().filter(term -> !"category".equalsIgnoreCase(term.getKey()))
					.collect(Collectors.toList()));
			source.getFilterTerms().removeAll(filterTerms);
			target.setQuery(getSearchQueryConverter().convert(source));
			target.setUrl(getVendorCategoryUrlResolver().resolve(getCommerceCategoryService().getCategoryForCode(categoryCode))
					+ buildUrlQueryString(source, target));
		}
	}

	protected UrlResolver<CategoryModel> getVendorCategoryUrlResolver()
	{
		return vendorCategoryUrlResolver;
	}

	@Required
	public void setVendorCategoryUrlResolver(UrlResolver<CategoryModel> vendorCategoryUrlResolver)
	{
		this.vendorCategoryUrlResolver = vendorCategoryUrlResolver;
	}

	protected CommerceCategoryService getCommerceCategoryService()
	{
		return commerceCategoryService;
	}

	@Required
	public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService)
	{
		this.commerceCategoryService = commerceCategoryService;
	}

}
