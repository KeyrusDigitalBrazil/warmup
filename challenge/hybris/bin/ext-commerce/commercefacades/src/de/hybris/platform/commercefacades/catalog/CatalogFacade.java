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
package de.hybris.platform.commercefacades.catalog;

import de.hybris.platform.commercefacades.catalog.data.CatalogData;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;

import java.util.List;
import java.util.Set;


/**
 * Catalog facade interface. Its main purpose is to retrieve catalog related DTOs using existing services.
 */
public interface CatalogFacade
{
	/**
	 * Returns catalog DTO for catalog id and options (BASIC, PRODUCTS, CATEGORIES)
	 *
	 * @param id
	 *           the catalog id
	 * @param options
	 *           the catalog options
	 * @return the catalog
	 */
	CatalogData getCatalogByIdAndOptions(String id, Set<CatalogOption> options);

	/**
	 * Returns catalog version DTO for catalog id, catalog version id and options (BASIC, PRODUCTS, CATEGORIES)
	 *
	 * @param catalogId
	 *           the catalog id
	 * @param catalogVersionId
	 *           the catalog version id
	 * @param opts
	 *           the catalog options
	 * @return the catalog version
	 */
	CatalogVersionData getCatalogVersionByIdAndOptions(String catalogId, String catalogVersionId, Set<CatalogOption> opts);

	/**
	 * Returns catalog DTOs for all catalogs and options (BASIC, PRODUCTS, CATEGORIES)
	 *
	 * @param opts
	 *           the catalog options
	 * @return the {@link List} of {@link CatalogData}
	 */
	List<CatalogData> getAllCatalogsWithOptions(Set<CatalogOption> opts);

	/**
	 * Returns category DTO for catalog id, catalog version id and category code and options (BASIC, PRODUCTS)
	 *
	 * @param catalogId
	 *           the catalog id
	 * @param catalogVersionId
	 *           the catalog version id
	 * @param categoryId
	 *           the category id
	 * @param page
	 *           the page options
	 * @param opts
	 *           the catalog options
	 * @return the category represented by an instance of {@link CategoryHierarchyData}
	 */
	CategoryHierarchyData getCategoryById(String catalogId, String catalogVersionId, String categoryId, PageOption page,
			Set<CatalogOption> opts);

	/**
	 * Returns product catalogs for the current base site
	 *
	 * @param opts
	 *           the catalog options
	 * @return the {@link List} of {@link CatalogData}
	 */
	List<CatalogData> getAllProductCatalogsForCurrentSite(Set<CatalogOption> opts);

	/**
	 * Returns current base site product catalog by id
	 *
	 * @param catalogId
	 *           the catalog id
	 * @param opts
	 *           the catalog options
	 * @return the product catalog
	 */
	CatalogData getProductCatalogForCurrentSite(String catalogId, Set<CatalogOption> opts);

	/**
	 * Returns catalog version DTO for catalog id, catalog version id, current base site and options (BASIC, PRODUCTS,
	 * CATEGORIES)
	 *
	 * @param catalogId
	 *           the catalog id
	 * @param catalogVersionId
	 *           the catalog version id
	 * @param opts
	 *           the catalog options
	 * @return the product catalog version
	 */
	CatalogVersionData getProductCatalogVersionForTheCurrentSite(String catalogId, String catalogVersionId, Set<CatalogOption> opts);
}
