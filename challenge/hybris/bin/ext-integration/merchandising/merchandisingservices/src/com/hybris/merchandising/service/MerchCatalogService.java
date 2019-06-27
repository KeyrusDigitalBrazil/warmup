/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.service;

import java.util.List;

import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

/**
 * MerchCatalogService is a service for the purpose of making catalog queries.
 *
 */
public interface MerchCatalogService {
	/**
	 * getCategories is a method for retrieving the category hierarchy (including subcategories).
	 * @param baseSite the base site we wish to retrieve the category hierarchy for.
	 * @param catalog  the catalog we wish to retrieve the category hierarchy from.
	 * @param catalogVersion the catalog version we wish to retrieve the category hierarchy for.
	 * @param baseCategoryUrl the URL we wish to use to access the category from.
	 * @return a List of {@link CategoryHierarchy} representing the categories.
	 */
	List<CategoryHierarchy> getCategories(String baseSite, String catalog, String catalogVersion, String baseCategoryUrl);
}
