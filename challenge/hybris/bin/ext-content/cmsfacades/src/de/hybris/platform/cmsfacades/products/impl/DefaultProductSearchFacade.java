/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.products.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.CategoryData;
import de.hybris.platform.cmsfacades.data.ProductData;
import de.hybris.platform.cmsfacades.products.ProductSearchFacade;
import de.hybris.platform.cmsfacades.products.service.ProductCategorySearchService;
import de.hybris.platform.cmsfacades.products.service.ProductSearchService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the {@link ProductSearchFacade} interface. 
 * 
 */
public class DefaultProductSearchFacade implements ProductSearchFacade
{
	private ProductService productService;
	private CategoryService categoryService;
	private Converter<ProductModel, ProductData> productDataConverter;
	private Converter<CategoryModel, CategoryData> categoryDataConverter;
	private CatalogVersionService catalogVersionService;
	private ProductCategorySearchService productCategorySearchService;
	private ProductSearchService productSearchService;
	
	@Override
	public ProductData getProductByCode(final String code)
	{
		return getProductDataConverter() //
				.convert(getProductService() //
						.getProductForCode(getSessionCatalogVersion(), code));
	}

	@Override
	public SearchResult<ProductData> findProducts(final String text, final PageableData pageableData)
	{
		final SearchResult<ProductModel> productSearchResult = getProductSearchService().findProducts(text, pageableData, getSessionCatalogVersion());

		return new SearchResultImpl<>(productSearchResult //
										.getResult()  //
										.stream() //
										.map(product -> getProductDataConverter().convert(product)) //
										.collect(Collectors.toList()),  //
									(int) productSearchResult.getTotalCount(), //
									productSearchResult.getRequestedCount(), //
									productSearchResult.getRequestedStart());
	}

	@Override
	public CategoryData getProductCategoryByCode(final String code)
	{
		return getCategoryDataConverter().convert(getCategoryService().getCategoryForCode(getSessionCatalogVersion(), code));
	}

	@Override
	public SearchResult<CategoryData> findProductCategories(final String text, final PageableData pageableData)
	{
		final SearchResult<CategoryModel> searchResult = getProductCategorySearchService().findProductCategories(text,
				pageableData, getSessionCatalogVersion());
		return new SearchResultImpl(
				searchResult.getResult()
						.stream()
						.map(categoryModel -> getCategoryDataConverter().convert(categoryModel))
						.collect(Collectors.toList()), searchResult.getTotalCount(), searchResult.getRequestedCount(), searchResult.getRequestedStart()); 
	}


	/**
	 * Get the current catalog version model from the session. 
	 * It must exist one and only one catalog version in the session.  
	 * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *            if no Catalog Version is defined in the requested user session. 
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if more than one Catalog Version is defined in the requested user session.
	 * @return the catalog version model from the request sessionService. 
	 */
	protected CatalogVersionModel getSessionCatalogVersion()
	{
		final Collection<CatalogVersionModel> sessionCatalogVersions = getCatalogVersionService().getSessionCatalogVersions();
		ServicesUtil.validateIfSingleResult(sessionCatalogVersions,
				"Catalog Version not identified in the session. Use CatalogVersionService#setSessionCatalogVersion(String, String) before using this method.",
				"It must exist only one Catalog Version set in the session. Please review the stack call and remove other catalog versions from the session.");
		return sessionCatalogVersions.stream().findFirst().get();
	}
	
	
	protected Converter<CategoryModel, CategoryData> getCategoryDataConverter()
	{
		return categoryDataConverter;
	}

	@Required
	public void setCategoryDataConverter(final Converter<CategoryModel, CategoryData> categoryDataConverter)
	{
		this.categoryDataConverter = categoryDataConverter;
	}

	protected Converter<ProductModel, ProductData> getProductDataConverter()
	{
		return productDataConverter;
	}

	@Required
	public void setProductDataConverter(final Converter<ProductModel, ProductData> productDataConverter)
	{
		this.productDataConverter = productDataConverter;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}


	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected ProductCategorySearchService getProductCategorySearchService()
	{
		return productCategorySearchService;
	}

	@Required
	public void setProductCategorySearchService(final ProductCategorySearchService productCategorySearchService)
	{
		this.productCategorySearchService = productCategorySearchService;
	}

	protected ProductSearchService getProductSearchService()
	{
		return productSearchService;
	}

	@Required
	public void setProductSearchService(final ProductSearchService productSearchService)
	{
		this.productSearchService = productSearchService;
	}
}
