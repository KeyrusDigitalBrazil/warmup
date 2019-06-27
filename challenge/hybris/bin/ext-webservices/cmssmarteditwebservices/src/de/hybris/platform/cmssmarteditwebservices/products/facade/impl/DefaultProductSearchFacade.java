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
package de.hybris.platform.cmssmarteditwebservices.products.facade.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.products.ProductSearchFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.cmssmarteditwebservices.data.CategoryData;
import de.hybris.platform.cmssmarteditwebservices.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.core.Registry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link de.hybris.platform.cmssmarteditwebservices.products.facade.ProductSearchFacade} interface. 
 */
public class DefaultProductSearchFacade implements de.hybris.platform.cmssmarteditwebservices.products.facade.ProductSearchFacade
{
	
	private ProductSearchFacade productSearchFacade;
	private Converter<de.hybris.platform.cmsfacades.data.ProductData, ProductData> productDataConverter;
	private Converter<de.hybris.platform.cmsfacades.data.CategoryData, CategoryData> categoryDataConverter;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private CMSAdminSiteService cmsAdminSiteService;
	private CatalogVersionService catalogVersionService;
	
	@Override
	public ProductData getProductByUid(final String uid)
	{
		final ProductModel product = (ProductModel) getUniqueItemIdentifierService() //
				.getItemModel(buildItemData(uid, ProductModel._TYPECODE)) //
				.orElseThrow(() -> new UnknownIdentifierException("Product cannot be found with UID " + uid));
		
		setCatalogVersionInSession(product.getCatalogVersion());
		return getProductDataConverter().convert(getProductSearchFacade().getProductByCode(product.getCode()));
	}

	@Override
	public SearchResult<ProductData> findProducts(final String text, final PageableData pageableData)
	{
		final SearchResult<de.hybris.platform.cmsfacades.data.ProductData> productSearchResult = getProductSearchFacade().findProducts(
				text, pageableData);
		final List<ProductData> products = productSearchResult.getResult().stream().map(
				productData -> getProductDataConverter().convert(productData)).collect(Collectors.toList());
		
		return new SearchResultImpl<>(products, productSearchResult.getTotalCount(), productSearchResult.getRequestedCount(), productSearchResult.getRequestedStart());
	}

	@Override
	public CategoryData getProductCategoryByUid(final String uid)
	{
		final CategoryModel category = (CategoryModel) getUniqueItemIdentifierService() //
				.getItemModel(buildItemData(uid, CategoryModel._TYPECODE)) //
				.orElseThrow(() -> new UnknownIdentifierException("Category cannot be found with UID " + uid));

		setCatalogVersionInSession(category.getCatalogVersion());
		
		return getCategoryDataConverter().convert(getProductSearchFacade().getProductCategoryByCode(category.getCode()));
	}

	@Override
	public SearchResult<CategoryData> findProductCategories(final String text, final PageableData pageableData)
	{
		final SearchResult<de.hybris.platform.cmsfacades.data.CategoryData> categorySearchResult = getProductSearchFacade().findProductCategories(
				text, pageableData);
		final List<CategoryData> categories = categorySearchResult.getResult().stream().map(
				categoryData -> getCategoryDataConverter().convert(categoryData)).collect(Collectors.toList());

		return new SearchResultImpl<>(categories, categorySearchResult.getTotalCount(), categorySearchResult.getRequestedCount(), categorySearchResult.getRequestedStart());
	}

	/**
	 * Sets the catalog version in the session so we can reuse ProductSearchFacade correctly.
	 * @param catalogVersion the catalog version to be set in the session
	 */
	protected void setCatalogVersionInSession(final CatalogVersionModel catalogVersion)
	{
		final String catalogId = catalogVersion.getCatalog().getId();
		final String version = catalogVersion.getVersion();
		try
		{
			getCmsAdminSiteService().setActiveCatalogVersion(catalogId, version);
			getCatalogVersionService().setSessionCatalogVersion(catalogId, version);
		}
		catch (CMSItemNotFoundException e)
		{
			throw new UnknownIdentifierException("Error setting catalog version in session", e);
		}
	}

	/**
	 * Returns the {@link ItemData} object that represents a product with its unique identifier code. 
	 * @param code the unique identifier code
	 * @param typeCode the TYPE CODE of the Item model
	 * @return the item data object that represents the product
	 */
	protected ItemData buildItemData(final String code, final String typeCode)
	{
		final ItemData itemData = (ItemData) Registry.getApplicationContext().getBean(CmssmarteditwebservicesConstants.ITEM_DATA_PROTOTYPE_BEAN);
		itemData.setItemId(code);
		itemData.setItemType(typeCode);
		return itemData;
	}

	protected ProductSearchFacade getProductSearchFacade()
	{
		return productSearchFacade;
	}

	@Required
	public void setProductSearchFacade(final ProductSearchFacade productSearchFacade)
	{
		this.productSearchFacade = productSearchFacade;
	}

	protected Converter<de.hybris.platform.cmsfacades.data.CategoryData, CategoryData> getCategoryDataConverter()
	{
		return categoryDataConverter;
	}

	@Required
	public void setCategoryDataConverter(
			final Converter<de.hybris.platform.cmsfacades.data.CategoryData, CategoryData> categoryDataConverter)
	{
		this.categoryDataConverter = categoryDataConverter;
	}

	protected Converter<de.hybris.platform.cmsfacades.data.ProductData, ProductData> getProductDataConverter()
	{
		return productDataConverter;
	}

	@Required
	public void setProductDataConverter(
			final Converter<de.hybris.platform.cmsfacades.data.ProductData, ProductData> productDataConverter)
	{
		this.productDataConverter = productDataConverter;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
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

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
