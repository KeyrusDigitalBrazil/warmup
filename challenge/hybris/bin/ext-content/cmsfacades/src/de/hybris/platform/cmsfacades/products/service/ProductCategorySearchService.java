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
package de.hybris.platform.cmsfacades.products.service;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Service Interface for Searching Product Categories
 */
public interface ProductCategorySearchService
{

	/**
	 * Method to find product categories using a free-text form. It also supports pagination.
	 * 
	 * @param text The free-text string to be used on the product category search
	 * @param pageableData the pagination object 
	 * @param catalogVersion the catalog version to search for the product categories from
	 * @return the search result object containing the resulting list and the pagination object.
	 * @throws InvalidNamedQueryException when the named query is invalid in the application context
	 * @Throws SearchExecutionNamedQueryException when there was a problem in the execution of the named query. 
	 */
	SearchResult<CategoryModel> findProductCategories(final String text, final PageableData pageableData, final CatalogVersionModel catalogVersion);
	
}
