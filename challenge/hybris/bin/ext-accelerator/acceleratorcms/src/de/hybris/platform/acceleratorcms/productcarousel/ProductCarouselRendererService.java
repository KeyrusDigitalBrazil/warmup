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
package de.hybris.platform.acceleratorcms.productcarousel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Service to fetch full list of products and categories of a product carousel component in preview mode.
 */
public interface ProductCarouselRendererService
{

	/**
	 * When in preview mode, we will swap the given products of a product carousel component for their counterpart in the
	 * catalog version in session for their respective catalogs. The session catalog version is not always the active
	 * version when used in CMS tooling.
	 *
	 * @param component
	 *           the product carousel component model
	 * @return a list of {@link ProductModel} for the catalog versions in session
	 */
	List<ProductModel> getDisplayableProducts(ProductCarouselComponentModel component);

	/**
	 * When in preview mode, we will swap the given products of a product category for their counterpart in the catalog
	 * version in session for their respective catalogs. The session catalog version is not always the active version
	 * when used in CMS tooling.
	 *
	 * @param category
	 *           the {@link ProductModel} go retrieve the products
	 * @return a list of {@link ProductModel} for the catalog versions in session
	 */
	List<ProductModel> getDisplayableProducts(CategoryModel category);

	/**
	 * Returns the full list categories without the session catalog version filtering out the ones from different
	 * versions. This is needed when the session catalog version is not the active version. This is possible through CMS
	 * tooling
	 *
	 * @param component
	 *           the product carousel component model
	 * @return a list of {@link CategoryModel} for the catalog versions in session
	 */
	List<CategoryModel> getListOfCategories(ProductCarouselComponentModel component);

}
