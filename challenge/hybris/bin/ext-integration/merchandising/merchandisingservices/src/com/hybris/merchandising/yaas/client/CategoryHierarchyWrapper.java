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
package com.hybris.merchandising.yaas.client;

import java.util.List;

import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

/**
 * This is a wrapper class for the generated {@link CategoryHierarchy} object.
 *
 */
public class CategoryHierarchyWrapper {
	List<CategoryHierarchy> categories;

	/**
	 * Default constructor, accepts a list of {@link CategoryHierarchy} objects.
	 * @param categories category list to store.
	 */
	public CategoryHierarchyWrapper(final List<CategoryHierarchy> categories) {
		this.categories = categories;
	}

	/**
	 * Retrieves the list of {@link CategoryHierarchy} objects stored within this wrapper.
	 * @return the categories stored.
	 */
	public List<CategoryHierarchy> getCategories() {
		return categories;
	}
}
