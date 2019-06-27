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
package de.hybris.platform.cmssmarteditwebservices.products.facade;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmssmarteditwebservices.data.CategoryData;
import de.hybris.platform.cmssmarteditwebservices.data.ProductData;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Product Search Facade is a facade for searching and retrieving products and categories.
 */
public interface ProductSearchFacade
{

	/**
	 * Method to get a product by its product uid.
	 * It expects the base site to be previously identified using {@link de.hybris.platform.site.BaseSiteService#setCurrentBaseSite(String, boolean)}
	 * and the catalog version to be stored in {@link SessionService} {@code CatalogConstants.SESSION_CATALOG_VERSIONS}.
	 * For your convenience, use {@link CatalogVersionService#setSessionCatalogVersion(String, String)}.
	 * @param uid the product uid
	 * @return the Product data object.
	 * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *            if no Product with the specified uid is found
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if more than one Product with the specified uid is found
	 * @throws IllegalArgumentException
	 *            if parameter uid is {@code null}.
	 */
	ProductData getProductByUid(final String uid);

	/**
	 * Method to find products using a free-text form. It also supports pagination.
	 * It expects the base site to be previously identified using {@link de.hybris.platform.site.BaseSiteService#setCurrentBaseSite(String, boolean)}
	 * and the catalog version to be stored in {@link SessionService} {@code CatalogConstants.SESSION_CATALOG_VERSIONS}.
	 * For your convenience, use {@link CatalogVersionService#setSessionCatalogVersion(String, String)}.
	 *
	 * @param text The free-text string to be used on the product search
	 * @param pageableData the pagination object
	 * @return the search result object.
	 */
	SearchResult<ProductData> findProducts(final String text, final PageableData pageableData);

	/**
	 * Method to get a category by its category uid.
	 * It expects the base site to be previously identified using {@link de.hybris.platform.site.BaseSiteService#setCurrentBaseSite(String, boolean)}
	 * and the catalog version to be stored in {@link SessionService} {@code CatalogConstants.SESSION_CATALOG_VERSIONS}.
	 * For your convenience, use {@link CatalogVersionService#setSessionCatalogVersion(String, String)}.
	 *
	 * @param uid the product category uid
	 * @return the Category data object containing the resulting list and the pagination object.
	 * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *            if no Product Category with the specified uid is found
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if more than one Product Category with the specified uid is found
	 * @throws IllegalArgumentException
	 *            if parameter uid is {@code null}.
	 */
	CategoryData getProductCategoryByUid(final String uid);

	/**
	 * Method to find product categories using a free-text form. It also supports pagination.
	 * It expects the base site to be previously identified using {@link de.hybris.platform.site.BaseSiteService#setCurrentBaseSite(String, boolean)}
	 * and the catalog version to be stored in {@link SessionService} {@code CatalogConstants.SESSION_CATALOG_VERSIONS}.
	 * For your convenience, use {@link CatalogVersionService#setSessionCatalogVersion(String, String)}.
	 *
	 * @param text The free-text string to be used on the product category search
	 * @param pageableData the pagination object
	 * @return the search result object containing the resulting list and the pagination object.
	 */
	SearchResult<CategoryData> findProductCategories(final String text, final PageableData pageableData);

}
