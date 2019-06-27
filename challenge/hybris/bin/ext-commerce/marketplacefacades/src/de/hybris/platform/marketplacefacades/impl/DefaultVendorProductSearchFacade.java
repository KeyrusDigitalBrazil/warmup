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
package de.hybris.platform.marketplacefacades.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.impl.DefaultSolrProductSearchFacade;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplacefacades.VendorProductSearchFacade;
import de.hybris.platform.marketplacefacades.vendor.VendorFacade;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.product.MarketplaceProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;


/**
 * An implementation of {@link VendorProductSearchFacade}
 */
public class DefaultVendorProductSearchFacade extends DefaultSolrProductSearchFacade<ProductData>
		implements VendorProductSearchFacade
{

	private VendorFacade vendorFacade;
	private Converter<FacetData<SearchStateData>, VendorData> vendorCategoryFacetConverter;
	private Converter<CategoryModel, CategoryData> categoryConverter;
	private MarketplaceProductService marketplaceProductService;

	@Override
	public VendorData getVendorCategories(final String vendorCode)
	{
		final Optional<VendorData> optional = getVendorFacade().getVendorByCode(vendorCode);
		if (optional.isPresent())
		{
			final VendorData vendorData = optional.get();
			final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
			final SolrSearchQueryTermData solrSearchQueryTermData = new SolrSearchQueryTermData();
			solrSearchQueryTermData.setKey("vendor");
			solrSearchQueryTermData.setValue(vendorCode);
			searchQueryData.setFilterTerms(Collections.singletonList(solrSearchQueryTermData));

			final List<FacetData<SearchStateData>> facets = getProductCategorySearchPageConverter()
					.convert(getProductSearchService().searchAgain(searchQueryData, null)).getFacets();
			if (!CollectionUtils.isEmpty(facets))
			{
				return getVendorCategoryFacetConverter().convert(facets.get(0), vendorData);
			}
			else
			{
				final List<ProductModel> productlist = marketplaceProductService.getAllProductByVendor(vendorCode);
				convertVendorData(vendorData, productlist);
			}
			return vendorData;
		}

		return null;
	}

	private void convertVendorData(final VendorData vendorData, final List<ProductModel> productlist)
	{
		final Iterator<ProductModel> iter = productlist.iterator();
		while (iter.hasNext())
		{
			final ProductModel product = iter.next();
			final List<CategoryData> categoryList = Converters.convertAll(product.getSupercategories(), categoryConverter);
			if (!categoryList.isEmpty())
			{
				vendorData.setTopCategories(categoryList);
				vendorData.setCategories(categoryList);
				break;
			}
		}
	}

	protected VendorFacade getVendorFacade()
	{
		return vendorFacade;
	}

	@Required
	public void setVendorFacade(final VendorFacade vendorFacade)
	{
		this.vendorFacade = vendorFacade;
	}

	protected Converter<FacetData<SearchStateData>, VendorData> getVendorCategoryFacetConverter()
	{
		return vendorCategoryFacetConverter;
	}

	@Required
	public void setVendorCategoryFacetConverter(final Converter<FacetData<SearchStateData>, VendorData> vendorCategoryFacetConverter)
	{
		this.vendorCategoryFacetConverter = vendorCategoryFacetConverter;
	}

	protected Converter<CategoryModel, CategoryData> getCategoryConverter()
	{
		return categoryConverter;
	}

	@Required
	public void setCategoryConverter(final Converter<CategoryModel, CategoryData> categoryConverter)
	{
		this.categoryConverter = categoryConverter;
	}

	public MarketplaceProductService getMarketplaceProductService()
	{
		return marketplaceProductService;
	}

	@Required
	public void setMarketplaceProductService(final MarketplaceProductService marketplaceProductService)
	{
		this.marketplaceProductService = marketplaceProductService;
	}

}
