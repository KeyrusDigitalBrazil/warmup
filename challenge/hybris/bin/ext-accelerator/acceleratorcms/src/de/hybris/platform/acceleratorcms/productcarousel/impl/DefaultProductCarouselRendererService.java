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
package de.hybris.platform.acceleratorcms.productcarousel.impl;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.acceleratorcms.productcarousel.ProductCarouselRendererService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Service to fetch full list of products and categories of a product carousel component in preview mode.
 */
public class DefaultProductCarouselRendererService implements ProductCarouselRendererService
{

	private ProductService productService;

	private CatalogVersionService catalogVersionService;

	private ModelService modelService;

	@Override
	public List<ProductModel> getDisplayableProducts(final ProductCarouselComponentModel component)
	{
		return retrieveProductsForVersionsInSession(component.getProducts());
	}

	@Override
	public List<ProductModel> getDisplayableProducts(final CategoryModel category)
	{
		return retrieveProductsForVersionsInSession(category.getProducts());
	}

	@Override
	public List<CategoryModel> getListOfCategories(final ProductCarouselComponentModel component)
	{

		final ProductCarouselComponentModel refreshed = (ProductCarouselComponentModel) getModelService().get(component.getPk());
		return refreshed.getCategories();

	}

	/**
	 * When in preview mode, we will swap the given products for their counterpart in the catalog version in session for
	 * their respective catalogs. The session catalog version is not always the active version when used in CMS tooling
	 */
	protected List<ProductModel> retrieveProductsForVersionsInSession(final List<ProductModel> persistentProducts)
	{

		return persistentProducts.stream().map(productModel -> {

			final String code = productModel.getCode();
			final CatalogModel catalog = productModel.getCatalogVersion().getCatalog();
			final CatalogVersionModel sessionCatalogVersionForCatalog = getCatalogVersionService()
					.getSessionCatalogVersionForCatalog(catalog.getId());
			//because of the possibility for a product code to be in multiple ProductCatalog, we explicitly fetch the product with the version in session for the product's catalog
			try
			{
				return getProductService().getProductForCode(sessionCatalogVersionForCatalog, code);
			}
			catch (final UnknownIdentifierException e)
			{
				return productModel;
			}
		}).collect(toList());

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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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

}
