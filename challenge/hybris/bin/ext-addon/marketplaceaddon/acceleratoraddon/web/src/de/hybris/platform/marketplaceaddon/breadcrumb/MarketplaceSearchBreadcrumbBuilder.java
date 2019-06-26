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
package de.hybris.platform.marketplaceaddon.breadcrumb;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;



/**
 * Override {@link SearchBreadcrumbBuilder} to add a Breadcrumb when applied facets has only one vendor facet.
 */
public class MarketplaceSearchBreadcrumbBuilder extends SearchBreadcrumbBuilder
{

	private UrlResolver<VendorModel> vendorUrlResolver;
	private UrlResolver<CategoryModel> vendorCategoryUrlResolver;
	private VendorService vendorService;

	@Override
	public List<Breadcrumb> getBreadcrumbs(String categoryCode, ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		final List<BreadcrumbData<SearchStateData>> breadcrumbDatas = searchPageData.getBreadcrumbs().stream()
				.filter(bc -> "vendor".equalsIgnoreCase(bc.getFacetCode())).collect(Collectors.toList());
		
		// We also need to check if categoryCode is null here as in text search case, we won't get categoryCode imported.
		if (CollectionUtils.isNotEmpty(breadcrumbDatas) && breadcrumbDatas.size() == 1 && categoryCode != null)
		{
			final List<Breadcrumb> breadcrumbs = new ArrayList<>();

			final Breadcrumb vendorBreadcrumb = getVendorService().getVendorByCode(breadcrumbDatas.get(0).getFacetValueCode())
					.map(vendor -> getVendorUrlResolver().resolve(vendor))
					.map(vendorUrl -> new Breadcrumb(vendorUrl, breadcrumbDatas.get(0).getFacetValueName(), null)).orElseGet(null);

			breadcrumbs.add(vendorBreadcrumb);

			final CategoryModel category = getCommerceCategoryService().getCategoryForCode(categoryCode);
			final Breadcrumb categoryBreadcrumb = new Breadcrumb(getVendorCategoryUrlResolver().resolve(category),
					category.getName(), null);
			breadcrumbs.add(categoryBreadcrumb);

			return breadcrumbs;
		}

		return super.getBreadcrumbs(categoryCode, searchPageData);
	}




	protected UrlResolver<VendorModel> getVendorUrlResolver()
	{
		return vendorUrlResolver;
	}

	@Required
	public void setVendorUrlResolver(UrlResolver<VendorModel> vendorUrlResolver)
	{
		this.vendorUrlResolver = vendorUrlResolver;
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

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	@Required
	public void setVendorService(VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

}
